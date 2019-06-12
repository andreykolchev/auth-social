package com.pirates.auth.service

import com.pirates.auth.exception.ErrorException
import com.pirates.auth.exception.ErrorType
import com.pirates.auth.model.entity.HistoryEntity
import com.pirates.auth.repository.HistoryRepository
import com.pirates.auth.repository.OperationRepository
import com.pirates.chat.model.bpe.CommandMessage
import com.pirates.chat.model.bpe.ResponseDto
import com.pirates.chat.utils.timestampNowUTC
import com.pirates.chat.utils.toJson
import com.pirates.chat.utils.toUUID
import org.springframework.stereotype.Service

@Service
class OperationService(private val operationRepository: OperationRepository) {

    fun check(operationID: String) {
        if (!operationRepository.isOperationIdExists(operationID)) throw ErrorException(ErrorType.INVALID_OPERATION_ID)
    }

}