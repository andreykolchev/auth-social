package com.pirates.auth.controller

import com.pirates.auth.model.Constants.TARGET
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/")
class WebController {

    @GetMapping("/{$TARGET}")
    fun getIndex(@PathVariable(TARGET) target: String, model: Model): String {
        model.addAttribute(TARGET, target)
        return "index"
    }

    @GetMapping("/registration/{$TARGET}")
    fun getRegistration(@PathVariable(TARGET) target: String, model: Model): String {
        model.addAttribute(TARGET, target)
        return "registration"
    }

    @GetMapping("/forgot-password/{$TARGET}")
    fun getForgotPassword(@PathVariable(TARGET) target: String, model: Model): String {
        model.addAttribute(TARGET, target)
        return "forgot-password"
    }

    @GetMapping("/success-new-pwd/{$TARGET}")
    fun getSuccessNewPwd(@PathVariable(TARGET) target: String, model: Model): String {
        model.addAttribute(TARGET, target)
        return "success-new-pwd"
    }
}
