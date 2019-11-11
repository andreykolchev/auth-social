package com.pirates.auth.controller

import com.pirates.auth.exception.EnumException
import com.pirates.auth.exception.ErrorException
import com.pirates.auth.model.AuthUser
import com.pirates.auth.model.Constants.AUTH_PROVIDER
import com.pirates.auth.model.Constants.FIRST_NAME
import com.pirates.auth.model.Constants.LAST_NAME
import com.pirates.auth.model.Constants.LOGIN
import com.pirates.auth.model.Constants.OPERATION_ID
import com.pirates.auth.model.Constants.PASSWORD
import com.pirates.auth.service.AuthService
import com.pirates.auth.model.bpe.ResponseDto
import com.pirates.auth.model.bpe.getEnumExceptionResponseDto
import com.pirates.auth.model.bpe.getErrorExceptionResponseDto
import com.pirates.auth.model.bpe.getExceptionResponseDto
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@Controller
@RequestMapping("/oauth")
class AuthController(private val authService: AuthService) {

    @PostMapping("/login/{$OPERATION_ID}")
    fun login(@PathVariable(OPERATION_ID) operationID: String, req: HttpServletRequest): ResponseEntity<ResponseDto> {
        val user = AuthUser(
                provider = AUTH_PROVIDER,
                providerId = req.getParameter(LOGIN),
                email = req.getParameter(LOGIN),
                password = req.getParameter(PASSWORD),
                operationId = operationID,
                name = ""
        )
        return ResponseEntity(authService.login(user), HttpStatus.OK)
    }

    @PostMapping("/registration/{$OPERATION_ID}")
    fun registration(@PathVariable(OPERATION_ID) operationID: String, req: HttpServletRequest): ResponseEntity<ResponseDto> {
        val user = AuthUser(
                provider = AUTH_PROVIDER,
                providerId = req.getParameter(LOGIN),
                email = req.getParameter(LOGIN),
                password = req.getParameter(PASSWORD),
                name = (req.getParameter(FIRST_NAME) + " " + req.getParameter(LAST_NAME)).trim(),
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
