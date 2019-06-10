package com.pirates.auth.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories


@Configuration
@EnableRedisRepositories(basePackages = ["com.pirates.chat.redisRepository"])
class RedisConfig {

//    @Bean
//    fun redisConnectionFactory() = JedisConnectionFactory()
//
//    @RedisHash("Operation")
//    data class Operation(private val id: String? = null) : Serializable

}