package com.pirates.auth.config

import io.zeebe.client.ZeebeClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan(basePackages = ["com.pirates.auth.jobworker"])
class ZeebeConfig {

    @Value("\${zeebe.broker}")
    private val zeebeBrokerHost: String = ""

    @Bean
    fun zeebeClient(): ZeebeClient {
        return ZeebeClient.newClientBuilder()
                .brokerContactPoint(zeebeBrokerHost)
                .build()
    }

}
