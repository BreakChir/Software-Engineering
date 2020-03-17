package com.github.balashov.fitness.turnstile

import com.github.balashov.fitness.common.mockkGetExistUser
import com.github.balashov.fitness.model.User
import com.github.jasync.sql.db.SuspendingConnection
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.joda.time.LocalDateTime
import org.junit.Test
import kotlin.test.assertEquals

class TurnstileCommandDaoImplTest {

    @Test
    fun testProcessEnterWithLastEvent() = runBlocking {
        val userId = 1
        val userName = "Pavel"
        val endSubscriptionTime = LocalDateTime.parse("2022-01-01")
        val eventId = 1
        val enterTime = LocalDateTime.parse("2021-01-01")
        val eventType = TurnstileActionType.ENTER
        val lastEventId = 105
        val lastEventType = "EXIT"
        val lastEventTime = LocalDateTime.parse("2020-01-01")
        val connection = mockk<SuspendingConnection>()

        coEvery {
            connection.inTransaction(any<suspend (SuspendingConnection) -> User?>())
        }.coAnswers {
            val callback = args[0] as suspend (SuspendingConnection) -> User?
            val transaction = mockk<SuspendingConnection>()

            mockkGetExistUser(transaction, userId, userName, endSubscriptionTime, eventId)
            mockkGetExistTurnstileEvent(transaction, userId, userName, lastEventId, lastEventType, lastEventTime)
            mockkAddTurnstileEvent(transaction, userId, lastEventId + 1, eventType, enterTime)

            callback(transaction)
        }

        val turnstileCommand = TurnstileCommandDaoImpl(connection)
        val actualResult = turnstileCommand.processEnter(userId, enterTime)
        assertEquals(Unit, actualResult)
    }

    @Test
    fun testProcessEnterFirstTime() = runBlocking {
        val userId = 1
        val userName = "Pavel"
        val endSubscriptionTime = LocalDateTime.parse("2022-01-01")
        val eventId = 1
        val enterTime = LocalDateTime.parse("2021-01-01")
        val eventType = TurnstileActionType.ENTER
        val connection = mockk<SuspendingConnection>()

        coEvery {
            connection.inTransaction(any<suspend (SuspendingConnection) -> User?>())
        }.coAnswers {
            val callback = args[0] as suspend (SuspendingConnection) -> User?
            val transaction = mockk<SuspendingConnection>()

            mockkGetExistUser(transaction, userId, userName, endSubscriptionTime, eventId)
            mockkGetNotExistTurnstileEvent(transaction, userId)
            mockkAddTurnstileEvent(transaction, userId, 0, eventType, enterTime)

            callback(transaction)
        }

        val turnstileCommand = TurnstileCommandDaoImpl(connection)
        val actualResult = turnstileCommand.processEnter(userId, enterTime)
        assertEquals(Unit, actualResult)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testProcessEnterAfterEnter() = runBlocking {
        val userId = 1
        val userName = "Pavel"
        val endSubscriptionTime = LocalDateTime.parse("2022-01-01")
        val eventId = 1
        val enterTime = LocalDateTime.parse("2021-01-01")
        val eventType = TurnstileActionType.ENTER
        val lastEventId = 105
        val lastEventType = "ENTER"
        val lastEventTime = LocalDateTime.parse("2020-01-01")
        val connection = mockk<SuspendingConnection>()

        coEvery {
            connection.inTransaction(any<suspend (SuspendingConnection) -> User?>())
        }.coAnswers {
            val callback = args[0] as suspend (SuspendingConnection) -> User?
            val transaction = mockk<SuspendingConnection>()

            mockkGetExistUser(transaction, userId, userName, endSubscriptionTime, eventId)
            mockkGetExistTurnstileEvent(transaction, userId, userName, lastEventId, lastEventType, lastEventTime)
            mockkAddTurnstileEvent(transaction, userId, lastEventId + 1, eventType, enterTime)

            callback(transaction)
        }

        val turnstileCommand = TurnstileCommandDaoImpl(connection)
        turnstileCommand.processEnter(userId, enterTime)
    }

    @Test
    fun testProcessExitAfterEnter() = runBlocking {
        val userId = 1
        val userName = "Pavel"
        val endSubscriptionTime = LocalDateTime.parse("2022-01-01")
        val eventId = 1
        val exitTime = LocalDateTime.parse("2021-01-01")
        val eventType = TurnstileActionType.EXIT
        val lastEventId = 105
        val lastEventType = "ENTER"
        val lastEventTime = LocalDateTime.parse("2020-01-01")
        val connection = mockk<SuspendingConnection>()

        coEvery {
            connection.inTransaction(any<suspend (SuspendingConnection) -> User?>())
        }.coAnswers {
            val callback = args[0] as suspend (SuspendingConnection) -> User?
            val transaction = mockk<SuspendingConnection>()

            mockkGetExistUser(transaction, userId, userName, endSubscriptionTime, eventId)
            mockkGetExistTurnstileEvent(transaction, userId, userName, lastEventId, lastEventType, lastEventTime)
            mockkAddTurnstileEvent(transaction, userId, lastEventId + 1, eventType, exitTime)

            callback(transaction)
        }

        val turnstileCommand = TurnstileCommandDaoImpl(connection)
        val actualResult = turnstileCommand.processExit(userId, exitTime)
        val expectedResult = Pair(lastEventTime, lastEventId + 1)
        assertEquals(expectedResult, actualResult)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testProcessExitWithoutLastEvent(): Unit = runBlocking {
        val userId = 1
        val userName = "Pavel"
        val endSubscriptionTime = LocalDateTime.parse("2022-01-01")
        val eventId = 1
        val enterTime = LocalDateTime.parse("2021-01-01")
        val eventType = TurnstileActionType.ENTER
        val connection = mockk<SuspendingConnection>()

        coEvery {
            connection.inTransaction(any<suspend (SuspendingConnection) -> User?>())
        }.coAnswers {
            val callback = args[0] as suspend (SuspendingConnection) -> User?
            val transaction = mockk<SuspendingConnection>()

            mockkGetExistUser(transaction, userId, userName, endSubscriptionTime, eventId)
            mockkGetNotExistTurnstileEvent(transaction, userId)
            mockkAddTurnstileEvent(transaction, userId, 0, eventType, enterTime)

            callback(transaction)
        }

        val turnstileCommand = TurnstileCommandDaoImpl(connection)
        turnstileCommand.processExit(userId, enterTime)
        Unit
    }
}
