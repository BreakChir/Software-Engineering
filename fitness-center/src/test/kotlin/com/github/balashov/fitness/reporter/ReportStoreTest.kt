package com.github.balashov.fitness.reporter

import com.github.balashov.fitness.model.UserReport
import com.github.jasync.sql.db.SuspendingConnection
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.joda.time.LocalDateTime
import org.joda.time.Period
import org.junit.Assert.assertEquals
import org.junit.Test

class ReportStoreTest {
    @Test
    fun testGetEmptyReport() = runBlocking {
        val userId = 1

        val connection = mockk<SuspendingConnection>()
        val store = ReportStore(connection)

        mockkInitWithEmptyData(connection)

        store.init()

        val expectedResult = null
        val actualResult = store.getUserReport(userId)
        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun testGetReportWithEmptyInit() = runBlocking {
        val userId = 1
        val eventId = 1
        val startTime = LocalDateTime.parse("2021-01-01")
        val endTime = LocalDateTime.parse("2022-01-01")

        val connection = mockk<SuspendingConnection>()
        val store = ReportStore(connection)

        mockkInitWithEmptyData(connection)

        store.init()
        store.addVisit(userId, startTime, endTime, eventId)

        val expectedResult = UserReport(1, Period.years(1))
        val actualResult = store.getUserReport(userId)
        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun testGetEmptyReportWithInit() = runBlocking {
        val userId = 1
        val storeUserId = 2
        val storeTotalVisits = 10L
        val storeTotalTime = Period.years(2)
        val storeMaxExitId = 1

        val connection = mockk<SuspendingConnection>()
        val store = ReportStore(connection)

        mockkInitNotEmptyData(connection, storeUserId, storeTotalVisits, storeTotalTime, storeMaxExitId)

        store.init()

        val expectedResult = null
        val actualResult = store.getUserReport(userId)
        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun testGetReportWithInit() = runBlocking {
        val userId = 2
        val eventId = 2
        val startTime = LocalDateTime.parse("2021-01-01")
        val endTime = LocalDateTime.parse("2022-01-01")

        val storeTotalVisits = 10L
        val storeTotalTime = Period.years(2)
        val storeMaxExitId = 1

        val connection = mockk<SuspendingConnection>()
        val store = ReportStore(connection)

        mockkInitNotEmptyData(connection, userId, storeTotalVisits, storeTotalTime, storeMaxExitId)

        store.init()
        store.addVisit(userId, startTime, endTime, eventId)

        val expectedResult = UserReport(11, storeTotalTime + Period.years(1))
        val actualResult = store.getUserReport(userId)
        assertEquals(expectedResult, actualResult)
    }
}
