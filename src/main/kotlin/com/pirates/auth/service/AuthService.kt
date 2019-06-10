package com.pirates.auth.service

import com.pirates.auth.model.AuthUser
import com.pirates.chat.model.bpe.ResponseDto
import org.springframework.stereotype.Service

@Service
class AuthService(private val processService: ProcessService

) {
    fun login(login: AuthUser): ResponseDto {
        //          return processService.loginByProcess(user)
        return processService.loginByRest(login)
    }

    fun registration(registration: AuthUser): ResponseDto {
//            return processService.registrationByProcess(user)
        return processService.registrationByRest(registration)
    }

}
