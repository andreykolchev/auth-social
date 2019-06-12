package com.pirates.auth.repository

import org.redisson.api.RBucket
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Repository

@Repository
class OperationRepository(
        private val redissonClient: RedissonClient
) {

    fun isOperationIdExists(operationID: String): Boolean {
        val key = "${REDIS_AUTH_KEY}_$operationID"
        val bucket: RBucket<String> = redissonClient.getBucket<String>(key)
        return bucket.get() != null
    }

    companion object {
        private const val REDIS_AUTH_KEY = "auth"
    }
}