package com.pirates.auth.service

import com.pirates.auth.config.properties.AuthProperties
import com.pirates.auth.config.properties.RoutsProperties
import com.pirates.auth.exception.ErrorException
import com.pirates.auth.exception.ErrorType
import com.pirates.auth.model.AuthProvider.*
import com.pirates.auth.model.AuthUser
import com.pirates.auth.model.Constants
import com.pirates.auth.model.UserStatus
import com.pirates.auth.model.bpe.ResponseDto
import com.pirates.auth.model.entity.UserEntity
import com.pirates.auth.repository.UserRepository
import com.pirates.auth.utils.genUUID
import com.pirates.auth.utils.toJson
import com.pirates.auth.utils.toJsonNode
import com.pirates.auth.utils.toObject
import org.springframework.stereotype.Service

@Service
class Auth2Service(private val ap: AuthProperties,
                   private val rp: RoutsProperties,
                   private val restService: RestService,
                   private val storageService: StorageService,
                   private val userRepository: UserRepository) {

    fun getProviderAuthURL(provider: String): String {
        return when (valueOf(provider)) {
            facebook -> "${ap.facebook.authUri}?$CL_ID=${ap.facebook.clientID}&$REDIRECT=${ap.callbackUrl}/facebook"
            google -> "${ap.google.authUri}?$CL_ID=${ap.google.clientID}&$RESPONSE_TYPE&$SCOPE=${ap.google.scope}&$REDIRECT=${ap.callbackUrl}/google"
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
        val redirectUri = if (userEntity != null) {
            val code = genUUID()
            storageService.saveProviderIdByCode(code, userEntity.providerId)
            "${rp.redirectUrl}?target=$target&code=$code"
        } else {
            registration(user)
        }
        return redirectUri
    }

    fun registration(registration: AuthUser): String {
        //check user email and personID
        val registrationResponse = restService.postRegistrationCommand(registration)
        if (registrationResponse.errors != null) {
            val errorMessage = ResponseDto(id = registrationResponse.id, errors = registrationResponse.errors).toJson()
            throw ErrorException(ErrorType.INVALID_PASSWORD, errorMessage)
        }
        val userResponse = toObject(AuthUser::class.java, toJsonNode(registrationResponse.data!!))
        val userEntity = getUserEntity(userResponse)
        userRepository.save(userEntity)
        val code = genUUID()
        return "${rp.redirectUrl}?target=${registration.target}&code=$code"
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


    companion object {
        private const val CL_ID = "client_id"
        private const val CL_SECRET = "client_secret"
        private const val SCOPE = "scope"
        private const val REDIRECT = "redirect_uri"
        private const val CODE = "code"
        private const val TOKEN = "access_token"
        private const val RESPONSE_TYPE = "response_type=code"
        private const val GRAND_TYPE = "grant_type=authorization_code"
    }
}
