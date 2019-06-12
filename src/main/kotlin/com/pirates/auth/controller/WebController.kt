package com.pirates.auth.controller

import com.pirates.auth.model.Constants.OPERATION_ID
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/interaction")
class WebController {

    @GetMapping("/{$OPERATION_ID}")
    fun getIndex(@PathVariable(OPERATION_ID) operationID: String, model: Model): String {
        model.addAttribute(OPERATION_ID, operationID)
        return "index"
    }

    @GetMapping("/registration/{$OPERATION_ID}")
    fun getRegistration(@PathVariable(OPERATION_ID) operationID: String, model: Model): String {
        model.addAttribute(OPERATION_ID, operationID)
        return "registration"
    }

    @GetMapping("/forgot-password/{$OPERATION_ID}")
    fun getForgotPassword(@PathVariable(OPERATION_ID) operationID: String, model: Model): String {
        model.addAttribute(OPERATION_ID, operationID)
        return "forgot-password"
    }

    @GetMapping("/success-new-pwd/{$OPERATION_ID}")
    fun getSuccessNewPwd(@PathVariable(OPERATION_ID) operationID: String, model: Model): String {
        model.addAttribute(OPERATION_ID, operationID)
        return "success-new-pwd"
    }
}
