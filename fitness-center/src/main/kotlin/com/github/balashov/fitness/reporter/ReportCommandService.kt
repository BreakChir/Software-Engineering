package com.github.balashov.fitness.reporter

class ReportCommandService(private val reporter: ReporterCommandDao) {
    fun process(task: ReporterCommand): String = try {
        when (task) {
            is AddVisitCommand -> {
                reporter.addVisit(task.userId, task.startTime, task.endTime, task.eventId)
                "OK"
            }
        }
    } catch (e: Exception) {
        "Got error during executing: ${e.message}"
    }
}
