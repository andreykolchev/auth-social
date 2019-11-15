package com.pirates.auth.service

import com.fasterxml.jackson.databind.JsonNode
import com.pirates.auth.config.properties.Auth2Properties
import com.pirates.auth.model.AuthProvider.*
import com.pirates.auth.model.AuthUser
import com.pirates.auth.model.Constants
import com.pirates.auth.model.bpe.ResponseDto
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
                   private val storageService: StorageService,
                   private val processService: ProcessService) {

    fun getProviderAuthURL(provider: String, operationID: String): String {
        storageService.isOperationIdExists(operationID)
        return when (valueOf(provider)) {
            facebook -> "${prop.facebook.authUri}?$CL_ID=${prop.facebook.clientID}&$REDIRECT=${prop.callbackUri}/$provider"
            google -> "${prop.google.authUri}?$CL_ID=${prop.google.clientID}&$RESPONSE_TYPE&$SCOPE=${prop.google.scope}&$REDIRECT=${prop.callbackUri}/$provider"
        }
    }

    fun processProviderResponse(provider: String, code: String, operationID: String): ResponseDto {
        storageService.isOperationIdExists(operationID)
        val userData: JsonNode?
        when (valueOf(provider)) {
            facebook -> {
                val tokenURL = "${prop.facebook.tokenUri}?$CL_ID=${prop.facebook.clientID}&$CL_SECRET=${prop.facebook.clientSecret}&$REDIRECT=${prop.callbackUri}/$provider&$CODE=$code"
                val tokenResponse = restTemplate.getForEntity(tokenURL, JsonNode::class.java)
                val token = tokenResponse.body?.get(TOKEN)?.asText()
                val userUrl = "${prop.facebook.userInfoUri}&$TOKEN=$token"
                val userResponse = restTemplate.getForEntity(userUrl, JsonNode::class.java)
                userData = userResponse.body!!
            }
            google -> {
                val tokenURL = prop.google.tokenUri
                val headers = HttpHeaders()
                headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
                val tokenRequest = HttpEntity("$CODE=$code&$CL_ID=${prop.google.clientID}&$CL_SECRET=${prop.google.clientSecret}&$GRAND_TYPE&$REDIRECT=${prop.callbackUri}/$provider", headers)
                val tokenResponse = restTemplate.postForEntity(tokenURL, tokenRequest, JsonNode::class.java)
                val token = tokenResponse.body?.get(TOKEN)?.asText()
                val userUrl = "${prop.google.userInfoUri}&$TOKEN=$token"
                val userResponse = restTemplate.getForEntity(userUrl, JsonNode::class.java)
                userData = userResponse.body!!
            }
        }
        val user = AuthUser(
                operationId = operationID,
                provider = provider,
                providerId = userData[Constants.ID]!!.asText(),
                email = userData[Constants.EMAIL]!!.asText(),
                name = userData[Constants.NAME]!!.asText()
        )

        return processService.processProviderUserData(user)
    }

    companion object {
        private const val CL_ID = "client_id"
        private const val CL_SECRET = "client_secret"
        private const val SCOPE = "scope"
        private const val REDIRECT = "redirect_uri"
        private const val CODE = "code"
        private const val TOKEN = "access_token"
        private const val RESPONSE_TYPE = "response_type=code"
        private const val GRAND_TYPE = "grant_type=authorization_code"
    }
}
