package com.pirates.auth.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "routs")
data class RoutsProperties(
        var starterUrl: String = "",
        var redirectUrl: String = ""
)
