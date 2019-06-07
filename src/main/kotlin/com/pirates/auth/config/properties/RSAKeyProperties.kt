package com.pirates.auth.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "rsa")
data class RSAKeyProperties(

        var publicKey: String = "",

        var privateKey: String = ""
)


