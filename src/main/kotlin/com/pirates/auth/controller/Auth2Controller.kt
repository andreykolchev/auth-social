package com.pirates.auth.controller

import com.pirates.auth.exception.EnumException
import com.pirates.auth.exception.ErrorException
import com.pirates.auth.model.Constants.CODE
import com.pirates.auth.model.Constants.TARGET
import com.pirates.auth.model.Constants.PROVIDER
import com.pirates.auth.service.Auth2Service
import com.pirates.auth.model.bpe.ResponseDto
import com.pirates.auth.model.bpe.getEnumExceptionResponseDto
import com.pirates.auth.model.bpe.getErrorExceptionResponseDto
import com.pirates.auth.model.bpe.getExceptionResponseDto
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Controller
@RequestMapping("/oauth2")
class Auth2Controller(private val auth2Service: Auth2Service) {

    @GetMapping("/login/{$PROVIDER}/{$TARGET}")
    fun login(@PathVariable(PROVIDER) provider: String,
              @PathVariable(TARGET) target: String,
              req: HttpServletRequest,
              res: HttpServletResponse) {
        //set target to session
        req.session.setAttribute(TARGET, target)
        res.sendRedirect(auth2Service.getProviderAuthURL(provider, target))
    }

    @GetMapping("/callback/{$PROVIDER}")
    fun callback(@PathVariable(PROVIDER) provider: String,
                 req: HttpServletRequest): ResponseEntity<ResponseDto> {
        //get code from provider redirect
        val code = req.getParameter(CODE)
        //get target from session
        val target = req.session.getAttribute(TARGET).toString()
        return ResponseEntity(auth2Service.processProviderResponse(provider, code, target), HttpStatus.OK)
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
