package com.pirates.auth.service

import com.pirates.auth.exception.ErrorException
import com.pirates.auth.exception.ErrorType
import com.pirates.auth.jobworker.CommandWorker
import com.pirates.auth.model.AuthUser
import com.pirates.auth.model.UserStatus
import com.pirates.auth.model.entity.UserEntity
import com.pirates.auth.repository.UserRepository
import com.pirates.chat.model.bpe.ApiVersion
import com.pirates.chat.model.bpe.CommandMessage
import com.pirates.chat.model.bpe.CommandType
import com.pirates.chat.model.bpe.ResponseDto
import com.pirates.chat.utils.hashPassword
import com.pirates.chat.utils.toJsonNode
import com.pirates.chat.utils.toObject
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.util.*

@Service
class ProcessService(private val userRepository: UserRepository,
                     private val restTemplate: RestTemplate,
                     private val tokenService: TokenService,
                     private val commandWorker: CommandWorker

) {
    fun loginByProcess(login: AuthUser) {
        val userEntity = userRepository.getByProviderId(login.providerId!!) ?: throw ErrorException(ErrorType.DATA_NOT_FOUND)
        if (userEntity.hashedPassword?.equals(login.password?.hashPassword()) != true) throw ErrorException(ErrorType.INVALID_PASSWORD)
        val user = AuthUser(
                operationId = login.operationId,
                personId = UUID.fromString(userEntity.personId),
                provider = userEntity.provider,
                providerId = userEntity.providerId,
                email = userEntity.email!!,
                password = userEntity.hashedPassword,
                name = userEntity.name
        )
        val cm = CommandMessage(
                id = login.operationId,
                command = CommandType.LOGIN,
                context = toJsonNode(""),
                data = toJsonNode(user),
                version = ApiVersion.V_0_0_1
        )
        commandWorker.startZeebeProcess(cm)
    }

    fun registrationByProcess(registration: AuthUser) {

        val cm = CommandMessage(
                id = registration.operationId,
                command = CommandType.REGISTRATION,
                context = toJsonNode(""),
                data = toJsonNode(registration),
                version = ApiVersion.V_0_0_1
        )
        commandWorker.startZeebeProcess(cm)
    }

    fun loginByRest(login: AuthUser): ResponseDto {
        val userEntity = userRepository.getByProviderId(login.providerId!!) ?: throw ErrorException(ErrorType.DATA_NOT_FOUND)
        if (login.provider == "auth") {
            if (userEntity.hashedPassword?.equals(login.password?.hashPassword()) != true) throw ErrorException(ErrorType.INVALID_PASSWORD)
        }
        //check user personID and email in u-data
        val user = AuthUser(
                operationId = login.operationId,
                personId = UUID.fromString(userEntity.personId),
                provider = userEntity.provider,
                providerId = userEntity.providerId,
                email = userEntity.email!!,
                password = userEntity.hashedPassword,
                name = userEntity.name
        )
        val cm = CommandMessage(
                id = login.operationId,
                command = CommandType.LOGIN,
                context = toJsonNode(""),
                data = toJsonNode(user),
                version = ApiVersion.V_0_0_1
        )
        val uDataUrl = "http://localhost:8081/command"
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val uDataRequest = HttpEntity(cm, headers)
        val uDataResponse = restTemplate.postForEntity(uDataUrl, uDataRequest, ResponseDto::class.java)
        val responseBody = uDataResponse.body ?: throw ErrorException(ErrorType.INVALID_DATA)
        if (responseBody.errors != null) return ResponseDto(id = login.operationId, errors = responseBody.errors)

        val token = tokenService.getTokenByUserCredentials(userEntity)
        return ResponseDto(id = login.operationId, data = token)
    }

    fun registrationByRest(registration: AuthUser): ResponseDto {
        //check email and get personId from u-data
        val cm = CommandMessage(
                id = UUID.randomUUID().toString(),
                command = CommandType.REGISTRATION,
                context = toJsonNode(""),
                data = toJsonNode(registration),
                version = ApiVersion.V_0_0_1
        )
        val uDataUrl = "http://localhost:8081/command"
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val uDataRequest = HttpEntity(cm, headers)
        val uDataResponse = restTemplate.postForEntity(uDataUrl, uDataRequest, ResponseDto::class.java)
        val responseBody = uDataResponse.body ?: throw ErrorException(ErrorType.INVALID_DATA)
        if (responseBody.errors != null) return ResponseDto(id = registration.operationId, errors = responseBody.errors)
        val user = toObject(AuthUser::class.java, toJsonNode(responseBody.data!!))
        val userEntity = UserEntity(
                providerId = user.providerId,
                personId = user.personId!!.toString(),
                provider = user.provider,
                name = user.name,
                email = user.email,
                status = UserStatus.created.toString(),
                hashedPassword = registration.password?.hashPassword())
        userRepository.save(userEntity)
        val token = tokenService.getTokenByUserCredentials(userEntity)
        return ResponseDto(id = registration.operationId, data = token)
    }
}
