package com.pirates.auth.repository

import com.hazelcast.core.HazelcastInstance
import com.hazelcast.core.ITopic
import com.pirates.auth.config.StorageConfig
import org.springframework.stereotype.Repository

@Repository
class OperationRepository(storageConfig: StorageConfig) {

    private val hazelcastInstance: HazelcastInstance = storageConfig.hazelcastInstance()
    private val topic: ITopic<String>

    init {
        topic = hazelcastInstance.getTopic("auth-topic")
    }

    fun publishMessage(message: String) {
        topic.publish(message)
    }

    fun isOperationIdExists(operationID: String): Boolean {
        val key = "${REDIS_AUTH_KEY}_$operationID"
        val map = hazelcastInstance.getMap<String, String>("client-operation")
        return map.getValue(key) != null
    }

    companion object {
        private const val REDIS_AUTH_KEY = "auth"
    }

}