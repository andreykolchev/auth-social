package com.pirates.auth

import com.pirates.auth.config.ApplicationConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackageClasses = [ApplicationConfig::class])
class BidsApplication

fun main(args: Array<String>) {
    runApplication<BidsApplication>(*args)
}
