//package com.pirates.auth.service
//
//import com.pirates.auth.model.bpe.CommandMessage
//import com.pirates.auth.model.bpe.CommandType
//import com.pirates.auth.model.bpe.ResponseDto
//import com.pirates.auth.utils.toObject
//import org.springframework.stereotype.Service
//
//@Service
//class CommandService(private val historyService: HistoryService,
//                     private val userService: UserService,
//                     private val tokenService: TokenService) {
//
//    fun execute(cm: CommandMessage): ResponseDto {
//        val historyEntity = historyService.getHistory(cm)
//        var history: ResponseDto? = null
//        if (historyEntity != null) {
//            history = toObject(ResponseDto::class.java, historyEntity.outputData)
//        }
//        val response: ResponseDto
//        when (cm.command) {
//            CommandType.REGISTRATION -> {
//                response = history ?: userService.create(cm)
//                history ?: historyService.saveHistory(cm, response)
//            }
//        }
//        return response
//    }
//}