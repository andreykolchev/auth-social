package com.pirates.auth.config

import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration


@Configuration
@ComponentScan(basePackages = ["com.pirates.auth.repository"])
class RedisConfig {

    @Value("\${redis.host}")
    private val redisHost: String = ""

    @Value("\${redis.port}")
    private val redisPort: Int = 0

//    @Bean
//    fun redissonClient(): RedissonClient {
//        val redissonConfig = org.redisson.config.Config()
////        redissonConfig.useClusterServers().addNodeAddress("redis://$redisHost:$redisPort")
//        redissonConfig.useSingleServer().setAddress("redis://$redisHost:$redisPort")
//        return Redisson.create(redissonConfig)
//    }

}