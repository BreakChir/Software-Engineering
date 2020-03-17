package com.github.balashov.fitness.reporter

import org.joda.time.LocalDateTime

interface ReporterCommandDao {
    fun addVisit(userId: Int, startTime: LocalDateTime, endTime: LocalDateTime, eventId: Int)
}
