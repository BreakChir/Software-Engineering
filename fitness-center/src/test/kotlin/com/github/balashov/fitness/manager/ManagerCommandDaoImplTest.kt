package com.github.balashov.fitness.manager

import com.github.balashov.fitness.model.User
import com.github.balashov.fitness.sql.SQLQuery
import com.github.jasync.sql.db.QueryResult
import com.github.jasync.sql.db.ResultSet
import com.github.jasync.sql.db.RowData
import com.github.jasync.sql.db.SuspendingConnection
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.joda.time.LocalDateTime
import kotlin.test.assertEquals
import org.junit.Test

class ManagerCommandDaoImplTest {
    @Test
    fun testRegisterUser() = runBlocking {
        val userId = 1
        val userName = "Pavel"
        val nextMaxId = 100
        val curMaxId = 0
        val connection = mockk<SuspendingConnection>()

        coEvery {
            connection.inTransaction(any<suspend (SuspendingConnection) -> User?>())
        }.coAnswers {
            val callback = args[0] as suspend (SuspendingConnection) -> User?
            val transaction = mockk<SuspendingConnection>()

            val getIdRows = mockk<ResultSet>()
            val getIdRow = mockk<RowData>()
            every { getIdRows[0] }.returns(getIdRow)
            every { getIdRow.getInt("maxId") }.returns(curMaxId)
            coEvery {
                transaction.sendQuery(SQLQuery.getMaxIdSQL)
            }.returns(QueryResult(0, "OK", getIdRows))

            coEvery {
                transaction.sendPreparedStatement(SQLQuery.updateMaxIdSQL, listOf(nextMaxId, curMaxId))
            }.returns(QueryResult(0, "OK"))

            coEvery {
                transaction.sendPreparedStatement(SQLQuery.addUserSQL, listOf(userId, userName))
            }.returns(QueryResult(0, "OK"))

            callback(transaction)
        }

        val managerCommand = ManagerCommandDaoImpl(connection)
        val actualUserId = managerCommand.registerUser(userName)
        assertEquals(userId, actualUserId)
    }

    @Test
    fun testUpdateSubscriptionOneTime() = runBlocking {
        val userId = 1
        val userName = "Pavel"
        val userEndTime = LocalDateTime.parse("2020-01-01")
        val subscribeTime = LocalDateTime.parse("2021-01-01")
        val eventId = 1
        val connection = mockk<SuspendingConnection>()

        coEvery {
            connection.inTransaction(any<suspend (SuspendingConnection) -> User?>())
        }.coAnswers {
            val callback = args[0] as suspend (SuspendingConnection) -> User?
            val transaction = mockk<SuspendingConnection>()

            mockkUpdateSubscription(transaction, userId, userName, userEndTime, subscribeTime, eventId)

            callback(transaction)
        }

        val managerCommand = ManagerCommandDaoImpl(connection)
        val actualResult = managerCommand.updateSubscription(userId, subscribeTime)
        assertEquals(Unit, actualResult)
    }

    @Test
    fun testUpdateSubscriptionManyTimes() = runBlocking {
        val userId = 1
        val userName = "Pavel"
        val userEndTime = LocalDateTime.parse("2020-01-01")
        val subscribeFirstTime = LocalDateTime.parse("2021-01-01")
        val subscribeSecondTime = LocalDateTime.parse("2022-06-03")
        val subscribeThirdTime = LocalDateTime.parse("2021-09-11")
        val eventId = 1
        val connection = mockk<SuspendingConnection>()

        coEvery {
            connection.inTransaction(any<suspend (SuspendingConnection) -> User?>())
        }.coAnswers {
            val callback = args[0] as suspend (SuspendingConnection) -> User?
            val transaction = mockk<SuspendingConnection>()

            mockkUpdateSubscription(transaction, userId, userName, userEndTime, subscribeFirstTime, eventId)
            mockkUpdateSubscription(transaction, userId, userName, subscribeFirstTime, subscribeSecondTime, eventId)
            mockkUpdateSubscription(transaction, userId, userName, subscribeSecondTime, subscribeThirdTime, eventId)

            callback(transaction)
        }

        val managerCommand = ManagerCommandDaoImpl(connection)
        val actualFirstResult = managerCommand.updateSubscription(userId, subscribeFirstTime)
        assertEquals(Unit, actualFirstResult)
        val actualSecondResult = managerCommand.updateSubscription(userId, subscribeSecondTime)
        assertEquals(Unit, actualSecondResult)
        val actualThirdResult = managerCommand.updateSubscription(userId, subscribeThirdTime)
        assertEquals(Unit, actualThirdResult)
    }
}
