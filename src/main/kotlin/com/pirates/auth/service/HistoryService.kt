package com.pirates.auth.service

import com.pirates.auth.model.entity.HistoryEntity
import com.pirates.auth.repository.HistoryRepository
import com.pirates.chat.model.bpe.CommandMessage
import com.pirates.chat.model.bpe.ResponseDto
import com.pirates.chat.utils.timestampNowUTC
import com.pirates.chat.utils.toJson
import com.pirates.chat.utils.toUUID
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
                        operationDate = timestampNowUTC(),
                        command = input.command.value(),
                        inputData = toJson(input),
                        outputData = toJson(output)
                )
        )
    }

}