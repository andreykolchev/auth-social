package com.pirates.auth.service

import com.pirates.auth.config.properties.Auth2Properties
import com.pirates.auth.model.AuthUser
import com.pirates.chat.model.bpe.ResponseDto
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Service

@Service
@EnableConfigurationProperties(Auth2Properties::class)
class AuthService(private val prop: Auth2Properties,
                  private val processService: ProcessService
) {
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
