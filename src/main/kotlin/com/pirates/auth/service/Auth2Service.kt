package com.pirates.auth.service

import com.fasterxml.jackson.databind.JsonNode
import com.pirates.auth.config.properties.Auth2Properties
import com.pirates.auth.model.AuthProvider.*
import com.pirates.auth.model.AuthUser
import com.pirates.auth.repository.UserRepository
import com.pirates.chat.model.bpe.ResponseDto
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
@EnableConfigurationProperties(Auth2Properties::class)
class Auth2Service(private val prop: Auth2Properties,
                   private val restTemplate: RestTemplate,
//                   private val operationRedisRepository: OperationRedisRepository,
                   private val userRepository: UserRepository,
                   private val processService: ProcessService) {

    fun getProviderAuthURL(provider: String, operationID: String): String {
        checkOperationID(operationID)
        return when (valueOf(provider)) {
            facebook -> "${prop.facebook.authUri}?client_id=${prop.facebook.clientID}&redirect_uri=${prop.callbackUri}/$provider"
            google -> "${prop.google.authUri}?client_id=${prop.google.clientID}&response_type=code&scope=${prop.google.scope}&redirect_uri=${prop.callbackUri}/$provider"
        }
    }

    fun processProviderResponse(provider: String, code: String, operationID: String): ResponseDto {
        checkOperationID(operationID)
        val userData: JsonNode?
        when (valueOf(provider)) {
            facebook -> {
                val tokenURL = "${prop.facebook.tokenUri}?client_id=${prop.facebook.clientID}&client_secret=${prop.facebook.clientSecret}&redirect_uri=${prop.callbackUri}/$provider&code=$code"
                val tokenResponse = restTemplate.getForEntity(tokenURL, JsonNode::class.java)
                val token = tokenResponse.body?.get("access_token")?.asText()
                val userUrl = "${prop.facebook.userInfoUri}&access_token=$token"
                val userResponse = restTemplate.getForEntity(userUrl, JsonNode::class.java)
                userData = userResponse.body!!
            }
            google -> {
                val tokenURL = prop.google.tokenUri
                val headers = HttpHeaders()
                headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
                val tokenRequest = HttpEntity("code=$code&client_id=${prop.google.clientID}&client_secret=${prop.google.clientSecret}&grant_type=authorization_code&redirect_uri=${prop.callbackUri}/$provider", headers)
                val tokenResponse = restTemplate.postForEntity(tokenURL, tokenRequest, JsonNode::class.java)
                val token = tokenResponse.body?.get("access_token")?.asText()
                val userUrl = "${prop.google.userInfoUri}&access_token=$token"
                val userResponse = restTemplate.getForEntity(userUrl, JsonNode::class.java)
                userData = userResponse.body!!
            }
        }
        val user = AuthUser(
                provider = provider,
                providerId = userData["id"]!!.asText(),
                email = userData["email"]!!.asText(),
                name = userData["name"]!!.asText(),
                operationId = operationID
        )
        return if (userRepository.getByProviderId(user.providerId!!) != null) {
//            processService.loginByProcess(user)
            processService.loginByRest(user)
        } else {
//            processService.registrationByProcess(user)
            processService.registrationByRest(user)
        }
    }

    private fun checkOperationID(operationID: String) {
//        if (!operationRedisRepository.findById(operationID).isPresent) throw ErrorException(ErrorType.INVALID_OPERATION_ID)
    }
}
