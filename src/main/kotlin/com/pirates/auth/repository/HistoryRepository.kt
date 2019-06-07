package com.pirates.auth.repository

import com.datastax.driver.core.Session
import com.datastax.driver.core.querybuilder.QueryBuilder.*
import com.pirates.auth.model.entity.HistoryEntity
import org.springframework.stereotype.Service
import java.util.*

@Service
class HistoryRepository(private val cassandraSession: Session) {

    fun getHistory(operationId: UUID, command: String): HistoryEntity? {
        val query = select()
                .all()
                .from(TABLE)
                .where(eq(OPERATION_ID, operationId))
                .and(eq(COMMAND, command))
        val row = cassandraSession.execute(query).one()
        return if (row != null) HistoryEntity(
                row.getUUID(OPERATION_ID),
                row.getTimestamp(OPERATION_DATE),
                row.getString(COMMAND),
                row.getString(INPUT_DATA),
                row.getString(OUTPUT_DATA)) else null

    }

    fun saveHistory(entity: HistoryEntity): HistoryEntity {
        val insert = insertInto(TABLE)
        insert.value(OPERATION_ID, entity.operationId)
                .value(COMMAND, entity.command)
                .value(OPERATION_DATE, entity.operationDate)
                .value(INPUT_DATA, entity.inputData)
                .value(OUTPUT_DATA, entity.outputData)
        cassandraSession.execute(insert)
        return entity
    }


    companion object {
        private const val TABLE = "auth_history"
        private const val OPERATION_ID = "operation_id"
        private const val COMMAND = "command"
        private const val OPERATION_DATE = "operation_date"
        private const val INPUT_DATA = "input_data"
        private const val OUTPUT_DATA = "output_data"
    }

}

