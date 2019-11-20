package com.pirates.auth.config

import com.pirates.auth.config.properties.AuthProperties
import com.pirates.auth.config.properties.JWTProperties
import com.pirates.auth.config.properties.RoutsProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
@ComponentScan(basePackages = ["com.pirates.auth.service"])
@EnableConfigurationProperties(AuthProperties::class, JWTProperties::class, RoutsProperties::class)
class ServiceConfig {
    @Bean
    fun restTemplate(): RestTemplate {
        return RestTemplate()
    }

}
