package com.pirates.auth.service

import com.pirates.chat.model.bpe.CommandMessage
import com.pirates.chat.model.bpe.CommandType
import com.pirates.chat.model.bpe.ResponseDto
import com.pirates.chat.utils.toObject
import org.springframework.stereotype.Service

@Service
class CommandService(private val historyService: HistoryService,
                     private val userService: UserService) {

    fun execute(cm: CommandMessage): ResponseDto {
        val historyEntity = historyService.getHistory(cm)
        if (historyEntity != null) {
            return toObject(ResponseDto::class.java, historyEntity.outputData)
        } else {
            val response = when (cm.command) {
                CommandType.REGISTRATION -> userService.create(cm)
                CommandType.LOGIN -> userService.createToken(cm)
            }
            historyService.saveHistory(cm, response)
            return response
        }
    }
}