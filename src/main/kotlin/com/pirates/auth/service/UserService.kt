package com.pirates.auth.service

import com.pirates.auth.exception.ErrorException
import com.pirates.auth.exception.ErrorType
import com.pirates.auth.model.AuthDataRs
import com.pirates.auth.model.AuthUser
import com.pirates.auth.model.Constants.PERSON_ID
import com.pirates.auth.model.UserStatus
import com.pirates.auth.model.entity.UserEntity
import com.pirates.auth.repository.UserRepository
import com.pirates.chat.model.bpe.CommandMessage
import com.pirates.chat.model.bpe.ResponseDto
import com.pirates.chat.utils.createObjectNode
import com.pirates.chat.utils.toObject
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository,
                  private val tokenService: TokenService

) {

    fun create(cm: CommandMessage): ResponseDto {
        val user = toObject(AuthUser::class.java, cm.data)
        val userEntity = UserEntity(
                providerId = user.providerId,
                personId = user.personId.toString(),
                provider = user.provider,
                name = user.name,
                hashedPassword = user.hashedPassword,
                email = user.email,
                status = UserStatus.created.toString())

        userRepository.save(userEntity)
        val context = createObjectNode()
        context.put(PERSON_ID, userEntity.personId)
        val dataRs = AuthDataRs(token = tokenService.getTokenByUserCredentials(userEntity))
        return ResponseDto(id = cm.id, context = context, data = dataRs)
    }

    fun createToken(cm: CommandMessage): ResponseDto {
        val user = toObject(AuthUser::class.java, cm.data)
        val userEntity = userRepository.getByProviderId(user.providerId!!) ?: throw ErrorException(ErrorType.DATA_NOT_FOUND)
        if (userEntity.hashedPassword?.equals(user.hashedPassword) != true) throw ErrorException(ErrorType.INVALID_PASSWORD)
        val context = createObjectNode()
        context.put(PERSON_ID, userEntity.personId)
        val dataRs = AuthDataRs(token = tokenService.getTokenByUserCredentials(userEntity))
        return ResponseDto(id = cm.id, context = context, data = dataRs)
    }
}
