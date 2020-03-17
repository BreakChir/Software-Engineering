package com.github.balashov.fitness.database

import com.github.jasync.sql.db.SuspendingConnection
import org.joda.time.LocalDateTime

import com.github.balashov.fitness.model.User
import com.github.balashov.fitness.sql.SQLQuery.getUserSQL

abstract class DatabaseAbstract {
    protected suspend fun getUser(transaction: SuspendingConnection, userId: Int): Pair<User?, Int?> {
        val result = transaction.sendPreparedStatement(getUserSQL, listOf(userId)).rows
        return if (result.isEmpty()) {
            Pair(null, null)
        } else {
            val name = result[0].getString("name")!!
            val subscriptionEnd = result[0].getAs<LocalDateTime?>("endTime")
            val eventId = result[0].getInt("eventId")
            Pair(User(userId, name, subscriptionEnd), eventId)
        }
    }
}
