package com.pirates.auth.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories
import java.io.Serializable


@Configuration
@EnableRedisRepositories(basePackages = ["com.pirates.chat.redisRepository"])
class RedisConfig {

    @Bean
    fun redisConnectionFactory() = JedisConnectionFactory()

    @RedisHash("Operation")
    data class Operation(private val id: String? = null) : Serializable


}