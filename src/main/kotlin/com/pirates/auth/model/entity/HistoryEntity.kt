package com.pirates.auth.model.entity

import java.util.*

data class HistoryEntity(

        var operationId: UUID,

        var operationDate: Date,

        var command: String,

        var inputData: String,

        var outputData: String
)
