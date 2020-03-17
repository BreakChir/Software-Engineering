package com.github.balashov.fitness.turnstile

import com.github.balashov.fitness.sql.SQLQuery
import com.github.jasync.sql.db.QueryResult
import com.github.jasync.sql.db.ResultSet
import com.github.jasync.sql.db.RowData
import com.github.jasync.sql.db.SuspendingConnection
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import org.joda.time.LocalDateTime

fun mockkGetNotExistTurnstileEvent(connection: SuspendingConnection, userId: Int) {
    val rows = mockk<ResultSet>()
    every { rows.isEmpty() }.returns(true)

    coEvery {
        connection.sendPreparedStatement(SQLQuery.getTurnstileEvents, listOf(userId))
    }.returns(QueryResult(0, "OK", rows))
}

fun mockkGetExistTurnstileEvent(
    connection: SuspendingConnection,
    userId: Int,
    userName: String,
    eventId: Int,
    eventType: String,
    eventTime: LocalDateTime
) {
    val rows = mockk<ResultSet>()
    every { rows.isEmpty() }.returns(false)

    val row = mockk<RowData>()
    every { row.getString("name") }.returns(userName)
    every { row.getInt("eventId") }.returns(eventId)
    every { row.getString("eventType") }.returns(eventType)
    every { row.getAs<LocalDateTime?>("eventTime") }.returns(eventTime)
    every { rows[0] }.returns(row)

    coEvery {
        connection.sendPreparedStatement(SQLQuery.getTurnstileEvents, listOf(userId))
    }.returns(QueryResult(0, "OK", rows))
}

fun mockkAddTurnstileEvent(
    connection: SuspendingConnection,
    userId: Int,
    eventId: Int,
    eventType: TurnstileActionType,
    eventTime: LocalDateTime
) {
    coEvery {
        connection.sendPreparedStatement(SQLQuery.addTurnstileEvent, listOf(userId, eventId, eventType, eventTime))
    }.returns(QueryResult(0, "OK"))
}
