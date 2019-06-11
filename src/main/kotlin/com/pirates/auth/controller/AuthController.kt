package com.pirates.auth.controller

import com.pirates.auth.exception.EnumException
import com.pirates.auth.exception.ErrorException
import com.pirates.auth.model.AuthUser
import com.pirates.auth.service.AuthService
import com.pirates.chat.model.bpe.ResponseDto
import com.pirates.chat.model.bpe.getEnumExceptionResponseDto
import com.pirates.chat.model.bpe.getErrorExceptionResponseDto
import com.pirates.chat.model.bpe.getExceptionResponseDto
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@Controller
@RequestMapping("/oauth")
class AuthController(private val authService: AuthService) {

    @PostMapping("/login/{operationID}")
    fun login(@PathVariable(name = "operationID") operationID: String, req: HttpServletRequest): ResponseEntity<ResponseDto> {
        val user = AuthUser(
                provider = "auth",
                providerId = req.getParameter("login"),
                email = req.getParameter("login"),
                password = req.getParameter("password"),
                operationId = operationID
        )
        return ResponseEntity(authService.login(user), HttpStatus.OK)
    }

    @PostMapping("/registration/{operationID}")
    fun registration(@PathVariable(name = "operationID") operationID: String, req: HttpServletRequest): ResponseEntity<ResponseDto> {
        val user = AuthUser(
                provider = "auth",
                providerId = req.getParameter("login"),
                email = req.getParameter("login"),
                password = req.getParameter("password"),
                name = (req.getParameter("firstname") + " " + req.getParameter("lastname")).trim(),
                operationId = operationID
        )
        return ResponseEntity(authService.registration(user), HttpStatus.OK)
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
