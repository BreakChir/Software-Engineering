package com.github.balashov.fitness.reporter

import com.github.balashov.fitness.model.UserReport
import com.github.balashov.fitness.sql.SQLQuery.getUserReports
import com.github.jasync.sql.db.SuspendingConnection
import org.joda.time.LocalDateTime
import org.joda.time.Period
import java.util.concurrent.ConcurrentHashMap

class ReportStore(private val connection: SuspendingConnection) {
    private val state = ConcurrentHashMap<Int, Pair<UserReport, Int>>()

    suspend fun init() {
        val rawStats = connection.sendPreparedStatement(getUserReports).rows
        for (row in rawStats) {
            val userId = row.getInt("userId")!!
            val totalVisits = row.getLong("totalVisits")!!.toInt()
            val totalTime = row.getAs<Period>("totalTime")
            val lastEventId = row.getInt("maxExitId")!!.toInt()
            state[userId] = UserReport(totalVisits, totalTime) to lastEventId
        }
    }

    fun addVisit(userId: Int, startTime: LocalDateTime, endTime: LocalDateTime, eventId: Int) {
        val visitPeriod = Period.fieldDifference(startTime, endTime)
        state.compute(userId) { _, data ->
            if (data == null) {
                Pair(UserReport(1, visitPeriod), eventId)
            } else {
                val (report, lastEventId) = data
                if (eventId <= lastEventId) {
                    data
                } else {
                    val newReport = UserReport(report.totalVisits + 1, report.totalTimeSpent + visitPeriod)
                    Pair(newReport, eventId)
                }
            }
        }
    }

    fun getUserReport(userId: Int): UserReport? = state[userId]?.first
}
