package com.pirates.auth.service

import com.pirates.auth.config.properties.RoutsProperties
import com.pirates.auth.model.AuthProvider.*
import com.pirates.auth.model.AuthUser
import com.pirates.auth.model.Constants
import com.pirates.auth.model.UserStatus
import com.pirates.auth.model.entity.UserEntity
import com.pirates.auth.repository.UserRepository
import com.pirates.auth.utils.genUUID
import org.springframework.stereotype.Service

@Service
class Auth2Service(private val rp: RoutsProperties,
                   private val restService: RestService,
                   private val storageService: StorageService,
                   private val userRepository: UserRepository) {

    fun getProviderAuthURL(provider: String): String {
        return when (valueOf(provider)) {
            facebook -> restService.getFacebookAuthURL()
            google -> restService.getGoogleAuthURL()
        }
    }

    fun processProviderResponse(provider: String, auth2code: String, target: String): String {
        val userData = when (valueOf(provider)) {
            facebook -> {
                val token = restService.getFacebookToken(auth2code)
                restService.getFacebookUserInfo(token)
            }
            google -> {
                val token = restService.getGoogleToken(auth2code)
                restService.getGoogleUserInfo(token)
            }
        }
        val user = AuthUser(
                target = target,
                provider = provider,
                providerId = userData[Constants.ID]!!.asText(),
                email = userData[Constants.EMAIL]!!.asText(),
                name = userData[Constants.NAME]!!.asText()
        )

        val userEntity = userRepository.getByProviderId(providerId = user.providerId)
        return if (userEntity != null) {
            val code = genUUID()
            storageService.saveProviderIdByCode(code, userEntity.providerId)
            "${rp.redirectUrl}?target=$target&code=$code"
        } else {
            val userResponse = restService.postRegistrationCommand(user)
            val newUserEntity = getUserEntity(userResponse)
            userRepository.save(newUserEntity)
            val code = genUUID()
            storageService.saveProviderIdByCode(code, newUserEntity.providerId)
            "${rp.redirectUrl}?target=$target}&code=$code"
        }
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
