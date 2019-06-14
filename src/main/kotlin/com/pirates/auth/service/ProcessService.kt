package com.pirates.auth.service

import com.pirates.auth.exception.ErrorException
import com.pirates.auth.exception.ErrorType
import com.pirates.auth.jobworker.CommandWorker
import com.pirates.auth.model.AuthDataRs
import com.pirates.auth.model.AuthUser
import com.pirates.auth.model.Constants
import com.pirates.auth.model.Constants.AUTH_PROVIDER
import com.pirates.auth.model.UserStatus
import com.pirates.auth.model.entity.UserEntity
import com.pirates.auth.repository.UserRepository
import com.pirates.chat.model.bpe.ApiVersion
import com.pirates.chat.model.bpe.CommandMessage
import com.pirates.chat.model.bpe.CommandType
import com.pirates.chat.model.bpe.ResponseDto
import com.pirates.chat.utils.createObjectNode
import com.pirates.chat.utils.hashPassword
import com.pirates.chat.utils.toJsonNode
import com.pirates.chat.utils.toObject
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.util.*

@Service
class ProcessService(private val userRepository: UserRepository,
                     private val restTemplate: RestTemplate,
                     private val tokenService: TokenService,
                     private val commandWorker: CommandWorker

) {

    @Value("\${process.uDataUrl}")
    private val uDataUrl: String = ""

    @Value("\${process.wsUrl}")
    private val wsUrl: String = ""

    @Value("\${process.byProcess}")
    private val byProcess: Boolean = false

    fun processUserData(user: AuthUser): ResponseDto {
        val userEntity = userRepository.getByProviderId(user.providerId)
        return if (userEntity != null) {
            login(user, userEntity)
        } else {
            registration(user)
        }
    }

    fun login(login: AuthUser, userEntity: UserEntity): ResponseDto {
        return if (byProcess) {
            loginByProcess(login, userEntity)
        } else {
            loginByRest(login, userEntity)
        }
    }

    fun registration(registration: AuthUser): ResponseDto {
        return if (byProcess) {
            registrationByProcess(registration)
        } else {
            registrationByRest(registration)
        }
    }

    private fun loginByRest(login: AuthUser, userEntity: UserEntity): ResponseDto {
        //check user password for auth
        if (login.provider == AUTH_PROVIDER) {
            if (userEntity.hashedPassword?.equals(login.password?.hashPassword()) != true) throw ErrorException(ErrorType.INVALID_PASSWORD)
        }
        val token = tokenService.getTokenByUserCredentials(userEntity)
        return ResponseDto(id = login.operationId, data = token)
    }

    private fun registrationByRest(registration: AuthUser): ResponseDto {
        //check user email and personID
        userRepository.checkEmailRegistration(registration.email)
        //check user password for auth
        val hashedPassword = if (registration.provider == AUTH_PROVIDER) registration.password?.hashPassword() else null
        val user = AuthUser(
                operationId = registration.operationId,
                provider = registration.provider,
                providerId = registration.providerId,
                email = registration.email,
                hashedPassword = hashedPassword,
                name = registration.name
        )
        val cm = CommandMessage(
                id = UUID.randomUUID().toString(),
                command = CommandType.REGISTRATION,
                context = toJsonNode(""),
                data = toJsonNode(user),
                version = ApiVersion.V_0_0_1
        )
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val uDataRequest = HttpEntity(cm, headers)
        val uDataResponse = restTemplate.postForEntity(uDataUrl, uDataRequest, ResponseDto::class.java)
        val responseBody = uDataResponse.body ?: throw ErrorException(ErrorType.INVALID_DATA)
        if (responseBody.errors != null) return ResponseDto(id = registration.operationId, errors = responseBody.errors)
        val userResponse = toObject(AuthUser::class.java, toJsonNode(responseBody.data!!))
        val userEntity = UserEntity(
                providerId = userResponse.providerId,
                personId = userResponse.personId!!.toString(),
                provider = userResponse.provider,
                name = userResponse.name,
                email = userResponse.email,
                status = UserStatus.created.toString(),
                hashedPassword = userResponse.hashedPassword)
        userRepository.save(userEntity)
        val token = tokenService.getTokenByUserCredentials(userEntity)
        return ResponseDto(id = registration.operationId, data = token)
    }

    private fun loginByProcess(login: AuthUser, userEntity: UserEntity): ResponseDto {
        if (login.provider == AUTH_PROVIDER) {
            if (userEntity.hashedPassword?.equals(login.password?.hashPassword()) != true) throw ErrorException(ErrorType.INVALID_PASSWORD)
        }
        val context = createObjectNode()
        context.put(Constants.PERSON_ID, userEntity.personId)
        val data = ResponseDto(id = login.operationId, context = context, data = AuthDataRs(token = tokenService.getTokenByUserCredentials(userEntity)))
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val wsRequest = HttpEntity(data, headers)
        val uDataResponse = restTemplate.postForEntity(wsUrl, wsRequest, ResponseDto::class.java)
        if (uDataResponse.body?.errors != null) return ResponseDto(id = login.operationId, errors = uDataResponse.body?.errors)
        return ResponseDto()
    }

    private fun registrationByProcess(registration: AuthUser): ResponseDto {
        //check user email
        userRepository.checkEmailRegistration(registration.email)
        val hashedPassword = if (registration.provider == AUTH_PROVIDER) registration.password?.hashPassword() else null
        val user = AuthUser(
                operationId = registration.operationId,
                provider = registration.provider,
                providerId = registration.providerId,
                email = registration.email,
                hashedPassword = hashedPassword,
                name = registration.name
        )
        val cm = CommandMessage(
                id = registration.operationId,
                command = CommandType.REGISTRATION,
                context = toJsonNode(""),
                data = toJsonNode(user),
                version = ApiVersion.V_0_0_1
        )
        commandWorker.startZeebeProcess(cm)
        return ResponseDto()
    }
}
