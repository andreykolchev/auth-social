package com.pirates.auth.config

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(DatabaseConfig::class,
        ServiceConfig::class,
        WebMvcConfig::class,
        RedisConfig::class,
        ZeebeConfig::class,
        DatabaseConfig::class
)
class ApplicationConfig
