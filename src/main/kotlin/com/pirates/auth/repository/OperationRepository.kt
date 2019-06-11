package com.pirates.auth.repository

import org.redisson.api.RBucket
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Repository

@Repository
class OperationRepository(private val redissonClient: RedissonClient) {

    fun isOperationIdExists(operationID: String): Boolean {
        val bucket: RBucket<String> = redissonClient.getBucket("auth_$operationID")
        return bucket.get() != null
    }
}