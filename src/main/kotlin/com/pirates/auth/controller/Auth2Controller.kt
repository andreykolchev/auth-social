package com.pirates.auth.controller

import com.pirates.auth.exception.EnumException
import com.pirates.auth.exception.ErrorException
import com.pirates.auth.model.Constants.CODE
import com.pirates.auth.model.Constants.OPERATION_ID
import com.pirates.auth.model.Constants.PROVIDER
import com.pirates.auth.service.Auth2Service
import com.pirates.chat.model.bpe.ResponseDto
import com.pirates.chat.model.bpe.getEnumExceptionResponseDto
import com.pirates.chat.model.bpe.getErrorExceptionResponseDto
import com.pirates.chat.model.bpe.getExceptionResponseDto
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Controller
@RequestMapping("/oauth2")
class Auth2Controller(private val auth2Service: Auth2Service) {

    @GetMapping("/login/{$PROVIDER}/{$OPERATION_ID}")
    fun login(@PathVariable(PROVIDER) provider: String,
              @PathVariable(OPERATION_ID) operationID: String,
              req: HttpServletRequest,
              res: HttpServletResponse) {
        //set operationID to session
        req.session.setAttribute(OPERATION_ID, operationID)
        res.sendRedirect(auth2Service.getProviderAuthURL(provider, operationID))
    }

    @GetMapping("/callback/{$PROVIDER}")
    fun callback(@PathVariable(PROVIDER) provider: String,
                 req: HttpServletRequest): ResponseEntity<ResponseDto> {
        //get code from provider redirect
        val code = req.getParameter(CODE)
        //get operationID from session
        val operationID = req.session.getAttribute(OPERATION_ID).toString()
        return ResponseEntity(auth2Service.processProviderResponse(provider, code, operationID), HttpStatus.OK)
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
