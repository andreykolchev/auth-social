package com.pirates.auth.controller

import com.pirates.auth.exception.EnumException
import com.pirates.auth.exception.ErrorException
import com.pirates.auth.model.AuthUser
import com.pirates.auth.model.Constants.AUTH_PROVIDER
import com.pirates.auth.model.Constants.FIRST_NAME
import com.pirates.auth.model.Constants.LAST_NAME
import com.pirates.auth.model.Constants.LOGIN
import com.pirates.auth.model.Constants.PASSWORD
import com.pirates.auth.model.Constants.TARGET
import com.pirates.auth.model.bpe.ResponseDto
import com.pirates.auth.model.bpe.getEnumExceptionResponseDto
import com.pirates.auth.model.bpe.getErrorExceptionResponseDto
import com.pirates.auth.model.bpe.getExceptionResponseDto
import com.pirates.auth.service.AuthService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
@RequestMapping("/oauth")
class AuthController(private val authService: AuthService) {

    @PostMapping("/login/{$TARGET}")
    fun login(@PathVariable(TARGET) target: String, req: HttpServletRequest, res: HttpServletResponse) {
        val user = AuthUser(
                provider = AUTH_PROVIDER,
                providerId = req.getParameter(LOGIN),
                email = req.getParameter(LOGIN),
                password = req.getParameter(PASSWORD),
                target = target,
                name = ""
        )
        res.sendRedirect(authService.login(user))
    }

    @PostMapping("/registration/{$TARGET}")
    fun registration(@PathVariable(TARGET) target: String, req: HttpServletRequest, res: HttpServletResponse) {
        val user = AuthUser(
                provider = AUTH_PROVIDER,
                providerId = req.getParameter(LOGIN),
                email = req.getParameter(LOGIN),
                password = req.getParameter(PASSWORD),
                name = (req.getParameter(FIRST_NAME) + " " + req.getParameter(LAST_NAME)).trim(),
                target = target
        )
        res.sendRedirect(authService.registration(user))
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
