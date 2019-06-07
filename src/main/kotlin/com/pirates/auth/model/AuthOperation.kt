package com.pirates.auth.model

import java.sql.Timestamp
import java.util.*

data class AuthOperation(

        var id: UUID,

        var created: Timestamp

)
