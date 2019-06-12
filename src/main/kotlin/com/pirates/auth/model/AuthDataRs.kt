package com.pirates.auth.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
data class AuthDataRs @JsonCreator constructor(

        var token: String
)
