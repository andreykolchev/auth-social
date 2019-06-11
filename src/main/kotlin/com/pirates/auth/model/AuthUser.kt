package com.pirates.auth.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
data class AuthUser @JsonCreator constructor(

        var operationId: String,

        var provider: String? = null,

        var providerId: String? = null,

        var email: String,

        var password: String? = null,

        var hashedPassword: String? = null,

        var name: String? = null,

        var personId: UUID? = null
)
