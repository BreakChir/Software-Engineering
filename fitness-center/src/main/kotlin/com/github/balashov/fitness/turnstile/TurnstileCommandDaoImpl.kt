package com.github.balashov.fitness.turnstile

import com.github.balashov.fitness.database.DatabaseAbstract
import com.github.balashov.fitness.sql.SQLQuery.addTurnstileEvent
import com.github.balashov.fitness.sql.SQLQuery.getTurnstileEvents
import com.github.jasync.sql.db.SuspendingConnection
import org.joda.time.LocalDateTime

class TurnstileCommandDaoImpl(private val connection: SuspendingConnection) : TurnstileCommandDao, DatabaseAbstract() {
    override suspend fun processExit(userId: Int, time: LocalDateTime) = connection.inTransaction {
        val prevEvent = getLastEvent(it, userId).second
            ?: throw IllegalArgumentException("No previous event for userId = $userId")
        if (prevEvent.first.type != TurnstileActionType.ENTER) {
            throw IllegalArgumentException("Previous event must be ENTER for userId = $userId")
        }
        if (!prevEvent.first.time.isBefore(time)) {
            throw IllegalArgumentException("Can't add events earlier last one")
        }
        val newEventId = prevEvent.second + 1
        val sqlParams = listOf(userId, newEventId, TurnstileActionType.EXIT, time)
        it.sendPreparedStatement(addTurnstileEvent, sqlParams)
        Pair(prevEvent.first.time, newEventId)
    }

    override suspend fun processEnter(userId: Int, time: LocalDateTime) = connection.inTransaction {
        val (user, _) = getUser(it, userId)
        if (user == null) {
            throw IllegalArgumentException("No user with id = $userId")
        }
        if (user.subscriptionEnd?.let { userTime -> !time.isBefore(userTime) } != false) {
            throw IllegalArgumentException("No suitable subscription for userId = $userId")
        }
        val prevEvent = getLastEvent(it, userId).second
        if (prevEvent?.first?.type == TurnstileActionType.ENTER) {
            throw IllegalArgumentException("Previous event was ENTER for userId = $userId")
        }
        val newEventId = prevEvent?.second?.let { eventId -> eventId + 1 } ?: 0
        val sqlParams = listOf(userId, newEventId, TurnstileActionType.ENTER, time)
        it.sendPreparedStatement(addTurnstileEvent, sqlParams)
        Unit
    }

    private suspend fun getLastEvent(
        transaction: SuspendingConnection,
        userId: Int
    ): Pair<String?, Pair<TurnstileAction, Int>?> {
        val result = transaction.sendPreparedStatement(getTurnstileEvents, listOf(userId)).rows
        if (result.isEmpty()) {
            return Pair(null, null)
        }
        val name = result[0].getString("name")!!
        val id = result[0].getInt("eventId") ?: return Pair(name, null)
        val type = TurnstileActionType.valueOf(result[0].getString("eventType")!!)
        val time = result[0].getAs<LocalDateTime>("eventTime")
        return Pair(name, Pair(TurnstileAction(type, time), id))
    }
}
