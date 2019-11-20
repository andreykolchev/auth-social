package com.pirates.auth.service

import com.pirates.auth.config.properties.JWTProperties
import com.pirates.auth.exception.ErrorException
import com.pirates.auth.exception.ErrorType
import com.pirates.auth.model.TokenType
import com.pirates.auth.model.entity.UserEntity
import com.pirates.auth.repository.UserRepository
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Service
import java.util.*

@Service
class TokenService(private val userRepository: UserRepository,
                   private val jwtService: JWTService) {


    fun getTokenByCode(code: String): String {
        val userEntity = userRepository.getByProviderId(providerId = login.providerId, provider = login.provider)
                ?: throw ErrorException(ErrorType.INVALID_EMAIL)
        val accessToken = jwtService.genToken(
                claims = mapOf<String, Any>(PERSON_ID_CLAIM to userEntity.personId, PROFILE_ID_CLAIM to userEntity.profileId),
                header = mapOf<String, Any>(HEADER_TOKEN_TYPE to TokenType.ACCESS.toString()),
                expiresOn = Date(System.currentTimeMillis() + 1000 * jwtProperties.accessLifeTime)
        )
    }

    fun getTokenByUserCredentials(userEntity: UserEntity): String {
        return jwtService.genToken(
                claims = mapOf<String, Any>(PERSON_ID_CLAIM to userEntity.personId, PROFILE_ID_CLAIM to userEntity.profileId),
                header = mapOf<String, Any>(HEADER_TOKEN_TYPE to TokenType.ACCESS.toString()),
                expiresOn = Date(System.currentTimeMillis() + 1000 * jwtProperties.accessLifeTime)
        )
    }

    fun verification(token: String) {
        val decodedJWT = jwtService.decodeJWT(token)
        val valueTokenTypeHeader = decodedJWT.getHeaderClaim(HEADER_TOKEN_TYPE).asString()
        if (TokenType.ACCESS.toString() != valueTokenTypeHeader) {
            throw ErrorException(ErrorType.INVALID_TOKEN_TYPE)
        }
    }

    fun getPersonId(token: String): UUID {
        val decodedJWT = jwtService.decodeJWT(token)
        return UUID.fromString(decodedJWT.getClaim(PERSON_ID_CLAIM).asString())
    }

    fun getProfileId(token: String): UUID {
        val decodedJWT = jwtService.decodeJWT(token)
        return UUID.fromString(decodedJWT.getClaim(PROFILE_ID_CLAIM).asString())
    }

    companion object {
        private const val HEADER_TOKEN_TYPE = "type"
        private const val PERSON_ID_CLAIM = "personID"
        private const val PROFILE_ID_CLAIM = "profileID"
    }
}
