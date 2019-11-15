package com.pirates.auth.config

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(DatabaseConfig::class,
        ServiceConfig::class,
        WebMvcConfig::class,
        StorageConfig::class,
        DatabaseConfig::class
)
class ApplicationConfig
