package com.pirates.auth.utils

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.apache.commons.codec.binary.Base64
import org.apache.commons.codec.digest.DigestUtils
import java.io.IOException
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField
import java.util.*


private object JsonMapper {

    val mapper: ObjectMapper = ObjectMapper().registerKotlinModule()
    var dateTimeFormatter: DateTimeFormatter

    init {
        val module = SimpleModule()
        mapper.registerModule(module)
        mapper.configure(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS, true)
        mapper.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true)
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        mapper.configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, true)

        mapper.nodeFactory = JsonNodeFactory.withExactBigDecimals(true)

        dateTimeFormatter = DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .append(DateTimeFormatter.ISO_LOCAL_DATE)
                .appendLiteral('T')
                .appendValue(ChronoField.HOUR_OF_DAY, 2)
                .appendLiteral(':')
                .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
                .optionalStart()
                .appendLiteral(':')
                .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
                .appendLiteral('Z')
                .toFormatter()
    }
}

fun genUUID(): String {
    return UUID.randomUUID().toString()
}

fun String.toUUID(): UUID {
    return UUID.fromString(this)
}

/*Date utils*/
fun String.toLocal(): LocalDateTime {
    return LocalDateTime.parse(this, JsonMapper.dateTimeFormatter)
}

fun LocalDateTime.toDate(): Date {
    return Date.from(this.toInstant(ZoneOffset.UTC))
}

fun localDateTimeNowUTC(): LocalDateTime {
    return LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC)
}

fun milliNowUTC(): Long {
    return localDateTimeNowUTC().toInstant(ZoneOffset.UTC).toEpochMilli()
}

fun localDateTimeToTimestamp(startDate: LocalDateTime): Timestamp {
    return Timestamp.from(startDate.toInstant(ZoneOffset.UTC))
}

fun timestampNowUTC(): Timestamp {
    return localDateTimeToTimestamp(localDateTimeNowUTC())
}

/*Json utils*/
fun String.toJsonNode(): JsonNode {
    try {
        return JsonMapper.mapper.readTree(this)
    } catch (e: IOException) {
        throw IllegalArgumentException(e)
    }
}

fun JsonNode.toObjectNode(): ObjectNode {
    return this as ObjectNode
}

fun createObjectNode(): ObjectNode {
    return JsonMapper.mapper.createObjectNode()
}


fun Any.toJson(): String {
    try {
        return JsonMapper.mapper.writeValueAsString(this)
    } catch (e: JsonProcessingException) {
        throw RuntimeException(e)
    }
}

fun <T> toJsonNode(obj: T): JsonNode {
    Objects.requireNonNull(obj)
    return JsonMapper.mapper.valueToTree(obj)
}

fun <T> toObject(clazz: Class<T>, json: String): T {
    try {
        return JsonMapper.mapper.readValue(json, clazz)
    } catch (e: IOException) {
        throw IllegalArgumentException(e)
    }
}

fun <T> toObject(clazz: Class<T>, json: JsonNode): T {
    try {
        return JsonMapper.mapper.treeToValue(json, clazz)
    } catch (e: IOException) {
        throw IllegalArgumentException(e)
    }
}

/*crypto utils*/
fun String.decodeBase64(): ByteArray {
    return Base64.decodeBase64(this)
}

fun String.encodeBase64(): String {
    return Base64.encodeBase64(this.toByteArray()).toString()
}

fun String.hashPassword(): String {
    return DigestUtils.sha1Hex(this + "Sd43f")
}

