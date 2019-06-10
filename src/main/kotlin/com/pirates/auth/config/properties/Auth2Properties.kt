package com.pirates.auth.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "oauth2")
data class Auth2Properties(
        var ws: Boolean = false,
        var facebook: Facebook = Facebook(),
        var google: Google = Google(),
        var callbackUri: String = "",
        var redirectUri: String = ""
)

data class Facebook(
        var clientID: String = "",
        var clientSecret: String = "",
        var authUri: String = "",
        var tokenUri: String = "",
        var userInfoUri: String = ""
)

data class Google(
        var clientID: String = "",
        var clientSecret: String = "",
        var authUri: String = "",
        var tokenUri: String = "",
        var userInfoUri: String = "",
        var scope: String = ""
)