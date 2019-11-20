package com.pirates.auth.service

import com.pirates.auth.config.properties.RoutsProperties
import com.pirates.auth.exception.ErrorException
import com.pirates.auth.exception.ErrorType
import com.pirates.auth.model.AuthUser
import com.pirates.auth.model.Constants
import com.pirates.auth.model.UserStatus
import com.pirates.auth.model.entity.UserEntity
import com.pirates.auth.repository.UserRepository
import com.pirates.auth.utils.genUUID
import com.pirates.auth.utils.hashPassword
import org.springframework.stereotype.Service

@Service
class AuthService(private val routsProperties: RoutsProperties,
                  private val restService: RestService,
                  private val userRepository: UserRepository,
                  private val storageService: StorageService
) {

    fun login(login: AuthUser): String {
        val userEntity = userRepository.getByProviderId(providerId = login.providerId) ?: throw ErrorException(ErrorType.INVALID_EMAIL)
        if (userEntity.hashedPassword?.equals(login.password?.hashPassword()) != true) throw ErrorException(ErrorType.INVALID_PASSWORD)
        val code = genUUID()
        storageService.saveProviderIdByCode(code, userEntity.providerId)
        return "${routsProperties.redirectUrl}?target=${login.target}&code=$code"
    }

    fun registration(registration: AuthUser): String {
        //check user password for auth
        val hashedPassword = if (registration.provider == Constants.AUTH_PROVIDER) registration.password?.hashPassword() else null
        val user = registration.copy(hashedPassword = hashedPassword)
        val userResponse = restService.postRegistrationCommand(user)
        val userEntity = getUserEntity(userResponse)
        userRepository.save(userEntity)
        val code = genUUID()
        return "${routsProperties.redirectUrl}?target=${registration.target}&code=$code"
    }

    private fun getUserEntity(user: AuthUser): UserEntity {
        return UserEntity(
                providerId = user.providerId,
                personId = user.personId!!.toString(),
                provider = user.provider,
                name = user.name,
                email = user.email,
                status = UserStatus.created.toString(),
                hashedPassword = user.hashedPassword)
    }
}
