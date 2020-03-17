package com.github.balashov.fitness.reporter

import org.joda.time.LocalDateTime

sealed class ReporterCommand

data class AddVisitCommand(
    val userId: Int,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val eventId: Int
) : ReporterCommand()
