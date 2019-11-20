package com.pirates.auth.config

import com.pirates.auth.config.properties.Auth2Properties
import com.pirates.auth.config.properties.JWTProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
@ComponentScan(basePackages = ["com.pirates.auth.service"])
@EnableConfigurationProperties(Auth2Properties::class, JWTProperties::class)
class ServiceConfig {
    @Bean
    fun restTemplate(): RestTemplate {
        return RestTemplate()
    }

}
