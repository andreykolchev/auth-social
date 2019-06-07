package com.pirates.auth.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/interaction")
class WebController {

    @GetMapping("/{operationID}")
    fun getIndex(@PathVariable(name = "operationID") operationID: String, model: Model): String {
        model.addAttribute("operationID", operationID)
        return "index"
    }

    @GetMapping("/registration/{operationID}")
    fun getRegistration(@PathVariable(name = "operationID") operationID: String, model: Model): String {
        model.addAttribute("operationID", operationID)
        return "registration"
    }

    @GetMapping("/forgot-password/{operationID}")
    fun getForgotPassword(@PathVariable(name = "operationID") operationID: String, model: Model): String {
        model.addAttribute("operationID", operationID)
        return "forgot-password"
    }

    @GetMapping("/success-new-pwd/{operationID}")
    fun getSuccessNewPwd(@PathVariable(name = "operationID") operationID: String, model: Model): String {
        model.addAttribute("operationID", operationID)
        return "success-new-pwd"
    }
}
