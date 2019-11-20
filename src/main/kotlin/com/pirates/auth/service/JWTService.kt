package com.pirates.auth.service

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.TokenExpiredException
import com.auth0.jwt.interfaces.DecodedJWT
import com.pirates.auth.config.properties.JWTProperties
import com.pirates.auth.exception.ErrorException
import com.pirates.auth.exception.ErrorType
import com.pirates.auth.utils.decodeBase64
import org.springframework.stereotype.Service
import java.security.KeyFactory
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*

@Service
class JWTService(private val properties: JWTProperties) {

    private val algorithm: Algorithm
    private val verifier: JWTVerifier

    init {
        algorithm = Algorithm.RSA256(getPublicKey(properties.publicKey), getPrivateKey(properties.privateKey))
        verifier = JWT.require(algorithm).build()
    }

    fun genAccessToken(claims: Map<String, Any>, header: Map<String, Any>): String {
        return JWT.create()
                .also { jwt ->
                    claims.forEach { (key, value) -> jwt.withClaim(key, value.toString()) }
                }
                .withHeader(header)
                .withExpiresAt(Date(System.currentTimeMillis() + 1000 * properties.lifeTime.access))
                .sign(algorithm)
    }

    fun genRefreshToken(claims: Map<String, Any>, header: Map<String, Any>): String {
        return JWT.create()
                .also { jwt ->
                    claims.forEach { (key, value) -> jwt.withClaim(key, value.toString()) }
                }
                .withHeader(header)
                .withExpiresAt(Date(System.currentTimeMillis() + 1000 * properties.lifeTime.refresh))
                .sign(algorithm)
    }

    fun decodeJWT(encodedJWT: String): DecodedJWT {
        try {
            return verifier.verify(encodedJWT)
        } catch (ex: TokenExpiredException) {
            throw ErrorException(ErrorType.TOKEN_EXPIRED)
        } catch (ex: Exception) {
            throw ErrorException(ErrorType.TOKEN_VERIFICATION_ERROR)
        }
    }

    private fun getPublicKey(publicKey: String): RSAPublicKey {
        val publicKeyBytes = decodePublicKey(publicKey)
        val publicKeySpec = X509EncodedKeySpec(publicKeyBytes)
        val keyFactory = KeyFactory.getInstance(RSA_ALGORITHM)
        return keyFactory.generatePublic(publicKeySpec) as RSAPublicKey
    }

    private fun getPrivateKey(privateKey: String): RSAPrivateKey {
        val privateKeyBytes = decodePrivateKey(privateKey)
        val privateKeySpec = PKCS8EncodedKeySpec(privateKeyBytes)
        val keyFactory = KeyFactory.getInstance(RSA_ALGORITHM)
        return keyFactory.generatePrivate(privateKeySpec) as RSAPrivateKey
    }

    private fun decodePublicKey(key: String) = excludeBodyPublicKey(key).decodeBase64()

    private fun decodePrivateKey(key: String) = excludeBodyPrivateKey(key).decodeBase64()

    private fun excludeBodyPublicKey(publicKey: String): String {
        return publicKey.trim { it <= ' ' }
                .let { if (isValidPublicKeyFormat(it)) it else throw ErrorException(ErrorType.INVALID_PUBLIC_KEY_FORMAT) }
                .substring(BEGIN_PUBLIC_KEY.length, publicKey.length - END_PUBLIC_KEY.length)
                .let { formatBody(it) }
    }

    private fun excludeBodyPrivateKey(privateKey: String): String {
        return privateKey.trim { it <= ' ' }
                .let { if (isValidPrivateKeyFormat(it)) it else throw ErrorException(ErrorType.INVALID_PRIVATE_KEY_FORMAT) }
                .substring(BEGIN_PRIVATE_KEY.length, privateKey.length - END_PRIVATE_KEY.length)
                .let { formatBody(it) }
    }

    private fun isValidPublicKeyFormat(key: String): Boolean =
            key.startsWith(BEGIN_PUBLIC_KEY) && key.endsWith(END_PUBLIC_KEY)

    private fun isValidPrivateKeyFormat(key: String): Boolean =
            key.startsWith(BEGIN_PRIVATE_KEY) && key.endsWith(END_PRIVATE_KEY)

    private fun formatBody(body: String) = body.replace(NEW_LINE_PATTERN.toRegex(), "").trim { it <= ' ' }

    companion object {
        private const val RSA_ALGORITHM = "RSA"
        private const val BEGIN_PUBLIC_KEY = "-----BEGIN PUBLIC KEY-----"
        private const val END_PUBLIC_KEY = "-----END PUBLIC KEY-----"
        private const val BEGIN_PRIVATE_KEY = "-----BEGIN PRIVATE KEY-----"
        private const val END_PRIVATE_KEY = "-----END PRIVATE KEY-----"
        private const val NEW_LINE_PATTERN = "[\r\n]"
    }
}