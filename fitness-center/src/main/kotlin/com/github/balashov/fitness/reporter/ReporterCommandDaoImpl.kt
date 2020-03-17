package com.github.balashov.fitness.reporter

import org.joda.time.LocalDateTime

class ReporterCommandDaoImpl(private val state: ReportStore) : ReporterCommandDao {
    override fun addVisit(userId: Int, startTime: LocalDateTime, endTime: LocalDateTime, eventId: Int) =
        state.addVisit(userId, startTime, endTime, eventId)
}
