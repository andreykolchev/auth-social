package com.pirates.auth.service

import com.hazelcast.core.HazelcastInstance
import com.hazelcast.core.ITopic
import com.pirates.auth.config.StorageConfig
import com.pirates.auth.exception.ErrorException
import com.pirates.auth.exception.ErrorType
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class StorageService(storageConfig: StorageConfig) {

    private val hazelcastInstance: HazelcastInstance = storageConfig.hazelcastInstance()
    private val authTopic: ITopic<String>
    private val syncTopic: ITopic<String>

    init {
        authTopic = hazelcastInstance.getTopic("auth-topic")
        syncTopic = hazelcastInstance.getTopic("sync-response-topic")
    }

    fun publishMessage(operationId: String, message: String) {
        val map = hazelcastInstance.getMap<String, String>("auth-map")
        map.put(operationId, message, 10, TimeUnit.SECONDS)
        authTopic.publish(operationId)
    }

    fun isOperationIdExists(operationID: String) {
        val key = "${REDIS_AUTH_KEY}_$operationID"
        val map = hazelcastInstance.getMap<String, String>("client-operation")
        if (map.getValue(key) == null) throw ErrorException(ErrorType.INVALID_OPERATION_ID)
    }

    companion object {
        private const val REDIS_AUTH_KEY = "auth"
    }

}