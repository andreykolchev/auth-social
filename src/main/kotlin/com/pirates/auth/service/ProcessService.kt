//package com.pirates.auth.service
//
//import com.pirates.auth.exception.ErrorException
//import com.pirates.auth.exception.ErrorType
//import com.pirates.auth.model.AuthDataRs
//import com.pirates.auth.model.AuthUser
//import com.pirates.auth.model.Constants.AUTH_PROVIDER
//import com.pirates.auth.model.Constants.PERSON_ID
//import com.pirates.auth.model.Constants.PROFILE_ID
//import com.pirates.auth.model.UserStatus
//import com.pirates.auth.model.bpe.*
//import com.pirates.auth.model.entity.UserEntity
//import com.pirates.auth.repository.UserRepository
//import com.pirates.auth.utils.*
//import org.springframework.beans.factory.annotation.Value
//import org.springframework.http.HttpEntity
//import org.springframework.http.HttpHeaders
//import org.springframework.http.MediaType
//import org.springframework.stereotype.Service
//import org.springframework.web.client.RestTemplate
//
//@Service
//class ProcessService(private val userRepository: UserRepository,
//                     private val restTemplate: RestTemplate,
//                     private val tokenService: TokenService,
//                     private val storageService: StorageService
//) {
//
//    @Value("\${process.starterUrl}")
//    private val processStarterUrl: String = ""
//
//    @Value("\${process.wsUrl}")
//    private val wsUrl: String = ""
//
//    @Value("\${process.byProcess}")
//    private val byProcess: Boolean = false
//
//    fun processProviderUserData(user: AuthUser): String {
//        val userEntity = userRepository.getByProviderId(providerId = user.providerId, provider = user.provider)
//        return if (userEntity != null) {
//            login(user, userEntity)
//        } else {
//            registration(user)
//        }
//    }
//
//    fun login(login: AuthUser, userEntity: UserEntity): String {
//        if (login.provider == AUTH_PROVIDER) {
//            if (userEntity.hashedPassword?.equals(login.password?.hashPassword()) != true){
//                return getErrorExceptionResponseDto(ErrorException(ErrorType.INVALID_PASSWORD)).toJson()
//            }
//        }
//        return ""
//    }
//
//    fun registration(registration: AuthUser): String {
//        //check user password for auth
//        val hashedPassword = if (registration.provider == AUTH_PROVIDER) registration.password?.hashPassword() else null
//        //check user email and personID
//        val user = registration.copy(hashedPassword = hashedPassword)
//        val uDataResponse = uDataRegistration(user)
//        if (uDataResponse.errors != null) return ResponseDto(id = uDataResponse.id, errors = uDataResponse.errors).toJson()
//        val userResponse = toObject(AuthUser::class.java, toJsonNode(uDataResponse.data!!))
//        val userEntity = getUserEntity(userResponse)
//        userRepository.save(userEntity)
//        return ""
//    }
//
////    private fun loginByProcess(login: AuthUser, userEntity: UserEntity): ResponseDto {
////        if (login.provider == AUTH_PROVIDER) {
////            if (userEntity.hashedPassword?.equals(login.password?.hashPassword()) != true) throw ErrorException(ErrorType.INVALID_PASSWORD)
////        }
//////        val token = tokenService.getTokenByUserCredentials(userEntity)
//////        return wsSendToken(personId = userEntity.personId, profileId = userEntity.profileId, token = token, operationId = login.operationId)
////    }
////
////    private fun registrationByProcess(registration: AuthUser): ResponseDto {
////        //check user password for auth
////        val hashedPassword = if (registration.provider == AUTH_PROVIDER) registration.password?.hashPassword() else null
////        //check user email and personID
////        val user = registration.copy(hashedPassword = hashedPassword)
////        val uDataResponse = uDataRegistration(user, registration.operationId)
////        if (uDataResponse.errors != null) return ResponseDto(id = uDataResponse.id, errors = uDataResponse.errors)
////        val userResponse = toObject(AuthUser::class.java, toJsonNode(uDataResponse.data!!))
////        val userEntity = getUserEntity(userResponse)
////        userRepository.save(userEntity)
////        val token = tokenService.getTokenByUserCredentials(userEntity)
////        return wsSendToken(personId = userEntity.personId, profileId = userEntity.profileId, token = token, operationId = registration.operationId)
////    }
////
////    private fun loginByRest(login: AuthUser, userEntity: UserEntity): ResponseDto {
////        //check user password for auth
////        if (login.provider == AUTH_PROVIDER) {
////            if (userEntity.hashedPassword?.equals(login.password?.hashPassword()) != true) throw ErrorException(ErrorType.INVALID_PASSWORD)
////        }
////        val token = tokenService.getTokenByUserCredentials(userEntity)
////        return ResponseDto(id = login.operationId, data = token)
////    }
////
////    private fun registrationByRest(registration: AuthUser): ResponseDto {
////        //check user password for auth
////        val hashedPassword = if (registration.provider == AUTH_PROVIDER) registration.password?.hashPassword() else null
////        //check user email and personID
////        val user = registration.copy(hashedPassword = hashedPassword)
////        val uDataResponse = uDataRegistration(user, registration.operationId)
////        if (uDataResponse.errors != null) return ResponseDto(id = uDataResponse.id, errors = uDataResponse.errors)
////        val userResponse = toObject(AuthUser::class.java, toJsonNode(uDataResponse.data!!))
////        val userEntity = getUserEntity(userResponse)
////        userRepository.save(userEntity)
////        val token = tokenService.getTokenByUserCredentials(userEntity)
////        return ResponseDto(id = registration.operationId, data = token)
////    }
//
//    private fun uDataRegistration(user: AuthUser): ResponseDto {
//        val cm = CommandMessage(
//                id = genUUID(),
//                command = CommandType.REGISTRATION,
//                context = toJsonNode(""),
//                data = toJsonNode(user),
//                version = ApiVersion.V_0_0_1
//        )
//        val headers = HttpHeaders()
//        headers.contentType = MediaType.APPLICATION_JSON
//        val request = HttpEntity(cm, headers)
//        val response = restTemplate.postForEntity(uDataUrl, request, ResponseDto::class.java)
//        return response.body ?: throw ErrorException(ErrorType.INVALID_DATA)
//    }
//
//    private fun wsSendToken(personId: String, profileId: String, token: String, operationId: String): ResponseDto {
//        val context = createObjectNode()
//        context.put(PERSON_ID, personId)
//        context.put(PROFILE_ID, profileId)
//        val message = ResponseDto(
//                id = operationId,
//                context = context,
//                data = AuthDataRs(token = token))
//        storageService.publishMessage(operationId, message.toJson())
//        return ResponseDto(id = operationId, data = "ok")
//    }
//
//    private fun getUserEntity(user: AuthUser): UserEntity {
//        return UserEntity(
//                providerId = user.providerId,
//                personId = user.personId!!.toString(),
//                profileId = user.profileId!!.toString(),
//                provider = user.provider,
//                name = user.name,
//                email = user.email,
//                status = UserStatus.created.toString(),
//                hashedPassword = user.hashedPassword)
//    }
//}
