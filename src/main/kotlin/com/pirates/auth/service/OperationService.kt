package com.pirates.auth.service

import com.pirates.auth.exception.ErrorException
import com.pirates.auth.exception.ErrorType
import com.pirates.auth.repository.OperationRepository
import org.springframework.stereotype.Service

@Service
class OperationService(private val operationRepository: OperationRepository) {

    fun check(operationID: String) {
        if (!operationRepository.isOperationIdExists(operationID)) throw ErrorException(ErrorType.INVALID_OPERATION_ID)
    }

}