package com.pirates.auth.service

import com.pirates.auth.model.AuthDataRs
import com.pirates.auth.model.AuthUser
import com.pirates.auth.model.Constants.PERSON_ID
import com.pirates.auth.model.UserStatus
import com.pirates.auth.model.entity.UserEntity
import com.pirates.auth.repository.UserRepository
import com.pirates.auth.model.bpe.CommandMessage
import com.pirates.auth.model.bpe.ResponseDto
import com.pirates.auth.utils.createObjectNode
import com.pirates.auth.utils.toObject
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository,
                  private val tokenService: TokenService

) {

    fun create(cm: CommandMessage): ResponseDto {
        val user = toObject(AuthUser::class.java, cm.data)
        val userEntity = UserEntity(
                providerId = user.providerId,
                personId = user.personId!!.toString(),
                profileId = user.profileId!!.toString(),
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
}
