package com.pirates.auth.service

import com.pirates.auth.exception.ErrorException
import com.pirates.auth.exception.ErrorType
import com.pirates.auth.model.AuthUser
import com.pirates.auth.model.Constants
import com.pirates.auth.model.UserStatus
import com.pirates.auth.model.bpe.ApiVersion
import com.pirates.auth.model.bpe.CommandMessage
import com.pirates.auth.model.bpe.CommandType
import com.pirates.auth.model.bpe.ResponseDto
import com.pirates.auth.model.entity.UserEntity
import com.pirates.auth.repository.UserRepository
import com.pirates.auth.utils.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class AuthService(private val restTemplate: RestTemplate,
                  private val userRepository: UserRepository,
                  private val storageService: StorageService
) {

    @Value("\${process.starterUrl}")
    private val processStarterUrl: String = ""

    @Value("\${oauth2.redirectUri}")
    private val redirectUri: String = ""

    fun login(login: AuthUser): String {
        val userEntity = userRepository.getByProviderId(providerId = login.providerId, provider = login.provider)
                ?: throw ErrorException(ErrorType.INVALID_EMAIL)
        if (userEntity.hashedPassword?.equals(login.password?.hashPassword()) != true) throw ErrorException(ErrorType.INVALID_PASSWORD)
        val code = genUUID()
        storageService.saveProviderIdByCode(code, userEntity.providerId)
        return "$redirectUri?target=${login.target}&code=$code"
    }

    fun registration(registration: AuthUser): String {
        //check user password for auth
        val hashedPassword = if (registration.provider == Constants.AUTH_PROVIDER) registration.password?.hashPassword() else null
        //check user email and personID
        val registrationResponse = PostRegistrationCommand(registration.copy(hashedPassword = hashedPassword))
        if (registrationResponse.errors != null){
            val errorMessage = ResponseDto(id = registrationResponse.id, errors = registrationResponse.errors).toJson()
            throw ErrorException(ErrorType.INVALID_PASSWORD, errorMessage)
        }
        val userResponse = toObject(AuthUser::class.java, toJsonNode(registrationResponse.data!!))
        val userEntity = getUserEntity(userResponse)
        userRepository.save(userEntity)
        val code = genUUID()
        return "$redirectUri?target=${registration.target}&code=$code"
    }

    private fun PostRegistrationCommand(user: AuthUser): ResponseDto {
        val cm = CommandMessage(
                id = genUUID(),
                command = CommandType.REGISTRATION,
                context = toJsonNode(""),
                data = toJsonNode(user),
                version = ApiVersion.V_0_0_1
        )
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val request = HttpEntity(cm, headers)
        val response = restTemplate.postForEntity(processStarterUrl, request, ResponseDto::class.java)
        return response.body ?: throw ErrorException(ErrorType.INVALID_DATA)
    }

    private fun getUserEntity(user: AuthUser): UserEntity {
        return UserEntity(
                providerId = user.providerId,
                personId = user.personId!!.toString(),
                profileId = user.profileId!!.toString(),
                provider = user.provider,
                name = user.name,
                email = user.email,
                status = UserStatus.created.toString(),
                hashedPassword = user.hashedPassword)
    }
}
