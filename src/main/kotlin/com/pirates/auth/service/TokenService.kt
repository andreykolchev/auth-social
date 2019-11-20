package com.pirates.auth.service

import com.pirates.auth.exception.ErrorException
import com.pirates.auth.exception.ErrorType
import com.pirates.auth.model.TokenType
import com.pirates.auth.repository.UserRepository
import com.pirates.auth.utils.toJson
import org.springframework.stereotype.Service

@Service
class TokenService(private val userRepository: UserRepository,
                   private val storageService: StorageService,
                   private val jwtService: JWTService) {


    fun getTokensByCode(code: String): String {
        val providerId = storageService.getProviderIdByCode(code)
        val userEntity = userRepository.getByProviderId(providerId = providerId) ?: throw ErrorException(ErrorType.INVALID_EMAIL)
        val tokens = jwtService.genTokens(userEntity)
        return tokens.toJson()
    }

    fun getTokensByRefreshToken(refreshToken: String): String {
        val decodedJWT = jwtService.decodeJWT(refreshToken)
        jwtService.verification(decodedJWT, TokenType.REFRESH)
        val providerId = jwtService.getProviderId(decodedJWT)
        val userEntity = userRepository.getByProviderId(providerId = providerId) ?: throw ErrorException(ErrorType.INVALID_EMAIL)
        val tokens = jwtService.genTokens(userEntity)
        return tokens.toJson()
    }

    fun verification(accessToken: String) {
        val decodedJWT = jwtService.decodeJWT(accessToken)
       jwtService.verification(decodedJWT, TokenType.ACCESS)
    }
}
