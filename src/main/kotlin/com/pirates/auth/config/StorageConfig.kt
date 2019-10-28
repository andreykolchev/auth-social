package com.pirates.auth.config

import com.hazelcast.client.HazelcastClient
import com.hazelcast.client.config.ClientConfig
import com.hazelcast.config.GroupConfig
import com.hazelcast.core.HazelcastInstance
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration


@Configuration
@ComponentScan(basePackages = ["com.pirates.auth.repository"])
class StorageConfig {

    @Value("\${hazelcast.cluster}")
    private val hazelcastCluster: String = ""
    @Value("\${hazelcast.group}")
    private val hazelcastGroup: String = ""
    @Value("\${hazelcast.password}")
    private val hazelcastPassword: String = ""

    @Bean
    fun hazelcastInstance(): HazelcastInstance {
        val config = ClientConfig()
        config.groupConfig = GroupConfig(hazelcastGroup, hazelcastPassword)
        config.networkConfig.addAddress(hazelcastCluster)
        return HazelcastClient.newHazelcastClient(config)
    }

}