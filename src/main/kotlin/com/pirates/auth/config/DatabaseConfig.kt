package com.pirates.auth.config

import com.datastax.driver.core.Cluster
import com.datastax.driver.core.PlainTextAuthProvider
import com.datastax.driver.core.Session
import com.pirates.auth.config.properties.CassandraProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(CassandraProperties::class)
@ComponentScan(basePackages = ["com.pirates.auth.repository"])
class DatabaseConfig constructor(private val properties: CassandraProperties) {

    internal val cluster: Cluster
        get() = Cluster.builder()
                .addContactPointsWithPorts(properties.getContactPoints())
                .withAuthProvider(PlainTextAuthProvider(properties.username, properties.password))
                .build()

    @Bean
    fun cassandraSession(): Session {
        return cluster.init().connect(properties.keyspaceName)
    }
}