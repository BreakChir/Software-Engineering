package com.github.balashov.fitness.reporter

class ReportQueryService(private val reporter: ReporterQueryDao) {
    fun process(task: ReporterQuery): String = try {
        when (task) {
            is GetUserReportQuery -> {
                val stats = reporter.getUserReport(task.userId)
                if (stats == null) {
                    "No such user"
                } else {
                    val normalizedTime = stats.totalTimeSpent.normalizedStandard()
                    val averageVisit = normalizedTime.toStandardMinutes().dividedBy(stats.totalVisits).minutes
                    "Total time spent: $normalizedTime\n" +
                            "Total visits: ${stats.totalVisits}\n" +
                            "Average visit: $averageVisit minutes\n"
                }
            }
        }
    } catch (e: Exception) {
        "Error while processing: ${e.message}"
    }
}
