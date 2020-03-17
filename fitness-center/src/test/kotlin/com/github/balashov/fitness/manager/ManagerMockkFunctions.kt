package com.github.balashov.fitness.manager

import com.github.balashov.fitness.common.mockkGetExistUser
import com.github.balashov.fitness.sql.SQLQuery
import com.github.jasync.sql.db.QueryResult
import com.github.jasync.sql.db.SuspendingConnection
import io.mockk.coEvery
import org.joda.time.LocalDateTime

fun mockkUpdateSubscription(
    connection: SuspendingConnection,
    userId: Int,
    userName: String,
    userEndTime: LocalDateTime,
    subscribeTime: LocalDateTime,
    eventId: Int
) {
    mockkGetExistUser(connection, userId, userName, userEndTime, eventId)

    coEvery {
        connection.sendPreparedStatement(
            SQLQuery.addSubscriptionSQL,
            listOf(userId, eventId + 1, subscribeTime)
        )
    }.returns(QueryResult(0, "OK"))
}
