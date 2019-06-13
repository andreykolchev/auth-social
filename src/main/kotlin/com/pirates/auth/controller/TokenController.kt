package com.pirates.auth.controller

import com.pirates.auth.exception.EnumException
import com.pirates.auth.exception.ErrorException
import com.pirates.auth.model.Constants.AUTHORIZATION_HEADER
import com.pirates.auth.model.Constants.AUTH_SCHEMA
import com.pirates.auth.service.TokenService
import com.pirates.chat.model.bpe.ResponseDto
import com.pirates.chat.model.bpe.getEnumExceptionResponseDto
import com.pirates.chat.model.bpe.getErrorExceptionResponseDto
import com.pirates.chat.model.bpe.getExceptionResponseDto
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/token")
class TokenController(private val tokenService: TokenService) {

    @GetMapping("/verification")
    fun verification(@RequestHeader(AUTHORIZATION_HEADER) header: String): ResponseEntity<String> {
        val token = header.substring(AUTH_SCHEMA.length)
        tokenService.verification(token)
        return ResponseEntity("ok", HttpStatus.OK)
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(Exception::class)
    fun exception(ex: Exception): ResponseDto {
        return when (ex) {
            is ErrorException -> getErrorExceptionResponseDto(ex)
            is EnumException -> getEnumExceptionResponseDto(ex)
            else -> getExceptionResponseDto(ex)
        }
    }

}