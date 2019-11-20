package com.pirates.auth.service

import com.hazelcast.core.HazelcastInstance
import com.hazelcast.core.IMap
import com.pirates.auth.config.StorageConfig
import com.pirates.auth.exception.ErrorException
import com.pirates.auth.exception.ErrorType
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class StorageService(storageConfig: StorageConfig) {

    private val hazelcastInstance: HazelcastInstance = storageConfig.hazelcastInstance()
    val authMap: IMap<String, String> = hazelcastInstance.getMap("auth-map")

    fun saveProviderIdByCode(code: String, providerId: String) {
        authMap.put(code, providerId, 10, TimeUnit.SECONDS)
    }

    fun getProviderIdByCode(code: String): String {
        return authMap.getValue(code)?: throw ErrorException(ErrorType.INVALID_CODE)
    }
}