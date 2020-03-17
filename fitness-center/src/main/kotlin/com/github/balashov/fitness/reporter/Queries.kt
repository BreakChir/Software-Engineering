package com.github.balashov.fitness.reporter

sealed class ReporterQuery

data class GetUserReportQuery(val userId: Int) : ReporterQuery()
