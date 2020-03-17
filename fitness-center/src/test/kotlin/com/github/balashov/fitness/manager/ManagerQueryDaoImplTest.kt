package com.github.balashov.fitness.manager

import com.github.balashov.fitness.common.mockkGetExistUser
import com.github.balashov.fitness.common.mockkGetNotExistUser
import com.github.balashov.fitness.model.User
import com.github.jasync.sql.db.SuspendingConnection
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.joda.time.LocalDateTime
import kotlin.test.assertEquals
import org.junit.Test

class ManagerQueryDaoImplTest {
    @Test
    fun testGetNotExistUser() = runBlocking {
        val userId = 1
        val connection = mockk<SuspendingConnection>()

        coEvery {
            connection.inTransaction(any<suspend (SuspendingConnection) -> User?>())
        }.coAnswers {
            val callback = args[0] as suspend (SuspendingConnection) -> User?
            val transaction = mockk<SuspendingConnection>()

            mockkGetNotExistUser(transaction, userId)

            callback(transaction)
        }

        val managerQuery = ManagerQueryDaoImpl(connection)
        val user = managerQuery.getUser(userId)
        assertEquals(null, user)
    }

    @Test
    fun testGetExistUser() = runBlocking {
        val userId = 1
        val userName = "Pavel"
        val userEndTime = LocalDateTime.parse("2021-01-01")
        val eventId = 1
        val connection = mockk<SuspendingConnection>()

        coEvery {
            connection.inTransaction(any<suspend (SuspendingConnection) -> User?>())
        }.coAnswers {
            val callback = args[0] as suspend (SuspendingConnection) -> User?
            val transaction = mockk<SuspendingConnection>()

            mockkGetExistUser(transaction, userId, userName, userEndTime, eventId)

            callback(transaction)
        }

        val managerQuery = ManagerQueryDaoImpl(connection)
        val actualUser = managerQuery.getUser(userId)
        val expectedUser = User(userId, userName, userEndTime)
        assertEquals(expectedUser, actualUser)
    }
}
