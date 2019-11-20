package com.pirates.auth.model.entity

data class UserEntity(

        var providerId: String,

        var provider: String,

        var personId: String,

        var status: String,

        var name: String,

        var email: String,

        var hashedPassword: String? = null
)
