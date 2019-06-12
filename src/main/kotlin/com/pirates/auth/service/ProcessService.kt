package com.pirates.auth.service

import com.pirates.auth.exception.ErrorException
import com.pirates.auth.exception.ErrorType
import com.pirates.auth.jobworker.CommandWorker
import com.pirates.auth.model.AuthUser
import com.pirates.auth.model.Constants.AUTH_PROVIDER
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
import org.springframework.beans.factory.annotation.Value
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

    @Value("\${uData.url}")
    private val uDataUrl: String = ""

    @Value("\${uData.process}")
    private val byProcess: Boolean = false


    fun login(login: AuthUser): ResponseDto {
        return if (byProcess) {
           loginByProcess(login)
        } else {
            loginByRest(login)
        }
    }

    fun registration(registration: AuthUser): ResponseDto {
        return if (byProcess) {
            registrationByProcess(registration)
        } else {
            registrationByRest(registration)
        }
    }

    private fun loginByProcess(login: AuthUser): ResponseDto {
        val userEntity = userRepository.getByProviderId(login.providerId!!) ?: throw ErrorException(ErrorType.DATA_NOT_FOUND)
        if (userEntity.hashedPassword?.equals(login.password?.hashPassword()) != true) throw ErrorException(ErrorType.INVALID_PASSWORD)
        val user = AuthUser(
                operationId = login.operationId,
                personId = UUID.fromString(userEntity.personId),
                provider = login.provider,
                providerId = login.providerId,
                email = login.email,
                hashedPassword = userEntity.hashedPassword,
                name = login.name
        )
        val cm = CommandMessage(
                id = login.operationId,
                command = CommandType.LOGIN,
                context = toJsonNode(""),
                data = toJsonNode(user),
                version = ApiVersion.V_0_0_1
        )
        commandWorker.startZeebeProcess(cm)
        return ResponseDto(id = login.operationId, data = "ok")
    }

    private fun registrationByProcess(registration: AuthUser): ResponseDto {
        val hashedPassword = if (registration.provider == AUTH_PROVIDER)  registration.password?.hashPassword() else null
        val user = AuthUser(
                operationId = registration.operationId,
                provider = registration.provider,
                providerId = registration.providerId,
                email = registration.email,
                hashedPassword = hashedPassword,
                name = registration.name
        )
        val cm = CommandMessage(
                id = registration.operationId,
                command = CommandType.REGISTRATION,
                context = toJsonNode(""),
                data = toJsonNode(user),
                version = ApiVersion.V_0_0_1
        )
        commandWorker.startZeebeProcess(cm)
        return ResponseDto(id = registration.operationId, data = "ok")
    }

    private fun loginByRest(login: AuthUser): ResponseDto {
        val userEntity = userRepository.getByProviderId(login.providerId!!) ?: throw ErrorException(ErrorType.DATA_NOT_FOUND)
        if (login.provider == AUTH_PROVIDER) {
            if (userEntity.hashedPassword?.equals(login.password?.hashPassword()) != true) throw ErrorException(ErrorType.INVALID_PASSWORD)
        }
        //check user personID and email in u-data
        val user = AuthUser(
                operationId = login.operationId,
                personId = UUID.fromString(userEntity.personId),
                provider = login.provider,
                providerId = login.providerId,
                email = login.email,
                hashedPassword = userEntity.hashedPassword,
                name = login.name
        )
        val cm = CommandMessage(
                id = login.operationId,
                command = CommandType.LOGIN,
                context = toJsonNode(""),
                data = toJsonNode(user),
                version = ApiVersion.V_0_0_1
        )
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val uDataRequest = HttpEntity(cm, headers)
        val uDataResponse = restTemplate.postForEntity(uDataUrl, uDataRequest, ResponseDto::class.java)
        val responseBody = uDataResponse.body ?: throw ErrorException(ErrorType.INVALID_DATA)
        if (responseBody.errors != null) return ResponseDto(id = login.operationId, errors = responseBody.errors)

        val token = tokenService.getTokenByUserCredentials(userEntity)
        return ResponseDto(id = login.operationId, data = token)
    }

    private fun registrationByRest(registration: AuthUser): ResponseDto {
        //check email and get personId from u-data
        val hashedPassword = if (registration.provider == AUTH_PROVIDER)  registration.password?.hashPassword() else null
        val user = AuthUser(
                operationId = registration.operationId,
                provider = registration.provider,
                providerId = registration.providerId,
                email = registration.email,
                hashedPassword = hashedPassword,
                name = registration.name
        )
        val cm = CommandMessage(
                id = UUID.randomUUID().toString(),
                command = CommandType.REGISTRATION,
                context = toJsonNode(""),
                data = toJsonNode(user),
                version = ApiVersion.V_0_0_1
        )
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val uDataRequest = HttpEntity(cm, headers)
        val uDataResponse = restTemplate.postForEntity(uDataUrl, uDataRequest, ResponseDto::class.java)
        val responseBody = uDataResponse.body ?: throw ErrorException(ErrorType.INVALID_DATA)
        if (responseBody.errors != null) return ResponseDto(id = registration.operationId, errors = responseBody.errors)
        val userResponse = toObject(AuthUser::class.java, toJsonNode(responseBody.data!!))
        val userEntity = UserEntity(
                providerId = userResponse.providerId,
                personId = userResponse.personId!!.toString(),
                provider = userResponse.provider,
                name = userResponse.name,
                email = userResponse.email,
                status = UserStatus.created.toString(),
                hashedPassword = userResponse.hashedPassword)
        userRepository.save(userEntity)
        val token = tokenService.getTokenByUserCredentials(userEntity)
        return ResponseDto(id = registration.operationId, data = token)
    }
}
