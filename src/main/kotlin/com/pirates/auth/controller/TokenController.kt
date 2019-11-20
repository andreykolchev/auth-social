package com.pirates.auth.controller

import com.pirates.auth.exception.EnumException
import com.pirates.auth.exception.ErrorException
import com.pirates.auth.model.Constants.AUTHORIZATION_HEADER
import com.pirates.auth.model.Constants.AUTH_SCHEMA
import com.pirates.auth.model.Constants.CODE
import com.pirates.auth.model.bpe.ResponseDto
import com.pirates.auth.model.bpe.getEnumExceptionResponseDto
import com.pirates.auth.model.bpe.getErrorExceptionResponseDto
import com.pirates.auth.model.bpe.getExceptionResponseDto
import com.pirates.auth.service.TokenService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/token")
class TokenController(private val tokenService: TokenService) {

    @GetMapping("/{$CODE}")
    fun getToken(@PathVariable(CODE) code: String): ResponseEntity<String> {
        return ResponseEntity(tokenService.getTokenByCode(code), HttpStatus.OK)
    }

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
