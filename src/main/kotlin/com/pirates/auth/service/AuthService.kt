package com.pirates.auth.service

import com.fasterxml.jackson.databind.JsonNode
import com.pirates.auth.config.properties.Auth2Properties
import com.pirates.auth.model.AuthUser
import com.pirates.auth.repository.UserRepository
import com.pirates.chat.model.bpe.ResponseDto
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Service

@Service
@EnableConfigurationProperties(Auth2Properties::class)
class AuthService(private val prop: Auth2Properties,
                  private val userRepository: UserRepository,
                  private val processService: ProcessService
) {

    fun processUserData(userData: JsonNode, provider: String, operationID: String): ResponseDto {
        val user = AuthUser(
                operationId = operationID,
                provider = provider,
                providerId = userData["id"]!!.asText(),
                email = userData["email"]!!.asText(),
                name = userData["name"]!!.asText()
        )
        return if (userRepository.getByProviderId(user.providerId!!) != null) {
            login(user)
        } else {
            registration(user)
        }
    }

    fun login(login: AuthUser): ResponseDto {
        return if (prop.ws) {
            processService.loginByProcess(login)
        } else {
            processService.loginByRest(login)
        }
    }

    fun registration(registration: AuthUser): ResponseDto {
        return if (prop.ws) {
            processService.registrationByProcess(registration)
        } else {
            processService.registrationByRest(registration)
        }
    }
}
