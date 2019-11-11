package com.pirates.auth.service

import com.pirates.auth.model.entity.HistoryEntity
import com.pirates.auth.repository.HistoryRepository
import com.pirates.auth.model.bpe.CommandMessage
import com.pirates.auth.model.bpe.ResponseDto
import com.pirates.auth.utils.timestampNowUTC
import com.pirates.auth.utils.toJson
import com.pirates.auth.utils.toUUID
import org.springframework.stereotype.Service

@Service
class HistoryService(private val historyRepository: HistoryRepository) {

    fun getHistory(cm: CommandMessage): HistoryEntity? {
        return historyRepository.getHistory(cm.id.toUUID(), cm.command.value())
    }

    fun saveHistory(input: CommandMessage, output: ResponseDto) {
        historyRepository.saveHistory(
                HistoryEntity(
                        operationId = input.id.toUUID(),
                        commandDate = timestampNowUTC(),
                        command = input.command.value(),
                        inputData = toJson(input),
                        outputData = toJson(output)
                )
        )
    }

}