package com.github.balashov.fitness.manager

import com.github.jasync.sql.db.SuspendingConnection
import org.joda.time.LocalDateTime
import java.util.concurrent.atomic.AtomicReference

import com.github.balashov.fitness.database.DatabaseAbstract
import com.github.balashov.fitness.sql.SQLQuery.addSubscriptionSQL
import com.github.balashov.fitness.sql.SQLQuery.addUserSQL
import com.github.balashov.fitness.sql.SQLQuery.getMaxIdSQL
import com.github.balashov.fitness.sql.SQLQuery.updateMaxIdSQL

class ManagerCommandDaoImpl(
    private val connection: SuspendingConnection
) : ManagerCommandDao, DatabaseAbstract() {

    data class IdInfo(val maxUsedId: Int, val maxId: Int)

    private val idInfoRef: AtomicReference<IdInfo> = AtomicReference(IdInfo(-1, -1))

    override suspend fun registerUser(name: String): Int = connection.inTransaction {
        val newId = getId(it)
        it.sendPreparedStatement(addUserSQL, listOf(newId, name))
        newId
    }

    override suspend fun updateSubscription(userId: Int, endTime: LocalDateTime) = connection.inTransaction {
        val (_, eventId) = getUser(it, userId)
        val newEventId = (eventId ?: 0) + 1
        it.sendPreparedStatement(addSubscriptionSQL, listOf(userId, newEventId, endTime))
        Unit
    }

    private suspend fun getId(transaction: SuspendingConnection): Int {
        while (true) {
            val idInfo = idInfoRef.get()
            if (idInfo.maxUsedId == idInfo.maxId) {
                val curMaxUid = if (idInfo.maxUsedId == -1) {
                    transaction.sendQuery(getMaxIdSQL).rows[0].getInt("maxId")!!
                } else {
                    idInfo.maxId
                }
                val nextMaxId = curMaxUid + 100
                transaction.sendPreparedStatement(updateMaxIdSQL, listOf(nextMaxId, curMaxUid))
                return curMaxUid + 1
            } else {
                val resultId = idInfo.maxUsedId + 1
                if (idInfoRef.compareAndSet(idInfo, IdInfo(resultId, idInfo.maxId))) {
                    return resultId
                }
            }
        }
    }
}
