package com.pirates.auth.service

import com.pirates.auth.config.properties.Auth2Properties
import com.pirates.auth.exception.ErrorException
import com.pirates.auth.exception.ErrorType
import com.pirates.auth.model.AuthUser
import com.pirates.auth.repository.UserRepository
import com.pirates.auth.model.bpe.ResponseDto
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Service

@Service
@EnableConfigurationProperties(Auth2Properties::class)
class AuthService(private val processService: ProcessService,
                  private val userRepository: UserRepository,
                  private val storageService: StorageService
) {

    fun login(login: AuthUser): ResponseDto {
        storageService.isOperationIdExists(login.operationId)
        val userEntity = userRepository.getByProviderId(providerId = login.providerId, provider = login.provider) ?: throw ErrorException(ErrorType.INVALID_EMAIL)
        return processService.login(login, userEntity)
    }

    fun registration(registration: AuthUser): ResponseDto {
        storageService.isOperationIdExists(registration.operationId)
        return processService.registration(registration)
    }
}
