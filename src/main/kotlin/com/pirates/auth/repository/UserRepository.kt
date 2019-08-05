package com.pirates.auth.repository

import com.datastax.driver.core.Session
import com.datastax.driver.core.querybuilder.QueryBuilder
import com.datastax.driver.core.querybuilder.QueryBuilder.eq
import com.pirates.auth.model.entity.UserEntity
import org.springframework.stereotype.Repository

@Repository
class UserRepository(private val cassandraSession: Session) {


    fun save(entity: UserEntity) {
        val insert = QueryBuilder.insertInto(USER_TABLE)
                .value(PROVIDER_ID, entity.providerId)
                .value(PROVIDER, entity.provider)
                .value(PERSON_ID, entity.personId)
                .value(STATUS, entity.status)
                .value(NAME, entity.name)
                .value(EMAIL, entity.email)
                .value(PASSWORD, entity.hashedPassword)
        cassandraSession.execute(insert)
    }

    fun getByProviderId(providerId: String, provider: String): UserEntity? {
        val query = QueryBuilder.select()
                .all()
                .from(USER_TABLE)
                .where(eq(PROVIDER_ID, providerId))
                .and(eq(PROVIDER, provider))
                .limit(1)
        val row = cassandraSession.execute(query).one()
        return if (row != null)
            UserEntity(
                    providerId = row.getString(PROVIDER_ID),
                    provider = row.getString(PROVIDER),
                    personId = row.getString(PERSON_ID),
                    status = row.getString(STATUS),
                    name = row.getString(NAME),
                    email = row.getString(EMAIL),
                    hashedPassword = row.getString(PASSWORD))
        else null
    }

    companion object {
        private const val USER_TABLE = "auth_user"
        private const val PROVIDER_ID = "provider_id"
        private const val PROVIDER = "provider"
        private const val PERSON_ID = "person_id"
        private const val STATUS = "status"
        private const val NAME = "name"
        private const val EMAIL = "email"
        private const val PASSWORD = "hashedpassword"
    }
}