package com.pirates.auth.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
@ComponentScan(basePackages = ["com.pirates.auth.service"])
class ServiceConfig {

    @Bean
    fun restTemplate(): RestTemplate {
        return RestTemplate()
    }

}
