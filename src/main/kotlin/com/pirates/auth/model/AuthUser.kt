package com.pirates.auth.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
data class AuthUser @JsonCreator constructor(

        var operationId: String,

        var provider: String,

        var providerId: String,

        var email: String,

        var name: String,

        var personId: UUID? = null,

        var password: String? = null,

        var hashedPassword: String? = null
)
