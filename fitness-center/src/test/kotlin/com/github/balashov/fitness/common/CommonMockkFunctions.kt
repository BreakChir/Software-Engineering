package com.github.balashov.fitness.common

import com.github.balashov.fitness.sql.SQLQuery
import com.github.jasync.sql.db.QueryResult
import com.github.jasync.sql.db.ResultSet
import com.github.jasync.sql.db.RowData
import com.github.jasync.sql.db.SuspendingConnection
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import org.joda.time.LocalDateTime

fun mockkGetNotExistUser(connection: SuspendingConnection, userId: Int) {
    val rows = mockk<ResultSet>()
    every { rows.isEmpty() }.returns(true)

    coEvery {
        connection.sendPreparedStatement(SQLQuery.getUserSQL, listOf(userId))
    }.returns(QueryResult(0, "OK", rows))
}

fun mockkGetExistUser(
    connection: SuspendingConnection,
    userId: Int,
    userName: String,
    userEndTime: LocalDateTime,
    eventId: Int
) {
    val rows = mockk<ResultSet>()
    every { rows.isEmpty() }.returns(false)

    val row = mockk<RowData>()
    every { row.getString("name") }.returns(userName)
    every { row.getAs<LocalDateTime?>("endTime") }.returns(userEndTime)
    every { row.getInt("eventId") }.returns(eventId)
    every { rows[0] }.returns(row)

    coEvery {
        connection.sendPreparedStatement(SQLQuery.getUserSQL, listOf(userId))
    }.returns(QueryResult(0, "OK", rows))
}
