import com.fasterxml.jackson.databind.JsonNode
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

//package com.pirates.auth.config
//
//import io.zeebe.client.ZeebeClient
//import org.springframework.beans.factory.annotation.Value
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.ComponentScan
//import org.springframework.context.annotation.Configuration
//
//@Configuration
//@ComponentScan(basePackages = ["com.pirates.auth.jobworker"])
//class ZeebeConfig {
//
//    @Value("\${zeebe.broker}")
//    private val zeebeBrokerHost: String = ""
//
//    @Bean
//    fun zeebeClient(): ZeebeClient {
//        return ZeebeClient.newClientBuilder()
//                .brokerContactPoint(zeebeBrokerHost)
//                .build()
//    }
//
//}

object ZeebeChannel {

    private val map = ConcurrentHashMap<String, ArrayBlockingQueue<JsonNode>>()

    private fun getQueue(key: String): ArrayBlockingQueue<JsonNode> {
        if (map[key] == null) {
            map[key] = ArrayBlockingQueue(1)
        }
        return map[key]!!
    }

    fun putData(key: String, value: JsonNode) {
        getQueue(key).put(value)
    }

    fun getData(key: String): JsonNode {
        val value = getQueue(key).poll(10, TimeUnit.SECONDS)!!
        map.remove(key)
        return value
    }
}
