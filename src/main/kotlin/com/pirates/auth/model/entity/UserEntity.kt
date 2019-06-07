package com.pirates.auth.model.entity

data class UserEntity(

        var providerId: String? = null,

        var personId: String? = null,

        var provider: String? = null,

        var status: String? = null,

        var name: String? = null,

        var email: String? = null,

        var hashedPassword: String? = null

)
