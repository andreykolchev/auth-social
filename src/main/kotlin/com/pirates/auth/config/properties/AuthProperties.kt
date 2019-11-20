package com.pirates.auth.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "oauth")
data class AuthProperties(
        var facebook: Facebook = Facebook(),
        var google: Google = Google(),
        var callbackUrl: String = ""
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