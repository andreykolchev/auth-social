package com.pirates.auth.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import java.net.InetSocketAddress
import java.net.InetSocketAddress.createUnresolved

@ConfigurationProperties(prefix = "cassandra")
data class CassandraProperties(
        var contactPoints: String?,
        var keyspaceName: String?,
        var port: Int?,
        var username: String?,
        var password: String?
) {
    fun getContactPoints(): List<InetSocketAddress> {
        return this.contactPoints!!
                .split(",".toRegex())
                .dropLastWhile { it.isEmpty() }
                .map { createUnresolved(it, port!!) }
                .toList()
    }
}

