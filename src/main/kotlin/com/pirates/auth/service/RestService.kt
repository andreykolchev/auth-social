package com.pirates.auth.service

import com.fasterxml.jackson.databind.JsonNode
import com.pirates.auth.config.properties.AuthProperties
import com.pirates.auth.config.properties.RoutsProperties
import com.pirates.auth.exception.ErrorException
import com.pirates.auth.exception.ErrorType
import com.pirates.auth.model.AuthUser
import com.pirates.auth.model.bpe.ApiVersion
import com.pirates.auth.model.bpe.CommandMessage
import com.pirates.auth.model.bpe.CommandType
import com.pirates.auth.model.bpe.ResponseDto
import com.pirates.auth.utils.genUUID
import com.pirates.auth.utils.toJson
import com.pirates.auth.utils.toJsonNode
import com.pirates.auth.utils.toObject
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class RestService(private val ap: AuthProperties,
                  private val rp: RoutsProperties,
                  private val restTemplate: RestTemplate
) {

    fun getFacebookToken(auth2code: String): String {
        val tokenURL = "${ap.facebook.tokenUri}?$CL_ID=${ap.facebook.clientID}&$CL_SECRET=${ap.facebook.clientSecret}&$REDIRECT=${ap.callbackUrl}/facebook&$CODE=$auth2code"
        val tokenResponse = restTemplate.getForEntity(tokenURL, JsonNode::class.java)
        return tokenResponse.body?.get(TOKEN)?.asText()!!
    }

    fun getFacebookUserInfo(token: String): JsonNode {
        val userUrl = "${ap.facebook.userInfoUri}&$TOKEN=$token"
        val userResponse = restTemplate.getForEntity(userUrl, JsonNode::class.java)
        return userResponse.body!!
    }

    fun getGoogleToken(auth2code: String): String {
        val tokenURL = ap.google.tokenUri
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
        val tokenRequest = HttpEntity("$CODE=$auth2code&$CL_ID=${ap.google.clientID}&$CL_SECRET=${ap.google.clientSecret}&$GRAND_TYPE&$REDIRECT=${ap.callbackUrl}/google", headers)
        val tokenResponse = restTemplate.postForEntity(tokenURL, tokenRequest, JsonNode::class.java)
        return tokenResponse.body?.get(TOKEN)?.asText()!!
    }

    fun getGoogleUserInfo(token: String): JsonNode {
        val userUrl = "${ap.google.userInfoUri}&$TOKEN=$token"
        val userResponse = restTemplate.getForEntity(userUrl, JsonNode::class.java)
        return userResponse.body!!
    }

    fun postRegistrationCommand(user: AuthUser): AuthUser {
        val cm = CommandMessage(
                id = genUUID(),
                command = CommandType.REGISTRATION,
                context = toJsonNode(""),
                data = toJsonNode(user),
                version = ApiVersion.V_0_0_1
        )
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val request = HttpEntity(cm, headers)
        val response = restTemplate.postForEntity(rp.starterUrl, request, ResponseDto::class.java)
        val registrationResponse = response.body ?: throw ErrorException(ErrorType.INVALID_DATA)
        if (registrationResponse.errors != null) {
            val errorMessage = ResponseDto(id = registrationResponse.id, errors = registrationResponse.errors).toJson()
            throw ErrorException(ErrorType.INVALID_PASSWORD, errorMessage)
        }
        return toObject(AuthUser::class.java, toJsonNode(registrationResponse.data!!))
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
