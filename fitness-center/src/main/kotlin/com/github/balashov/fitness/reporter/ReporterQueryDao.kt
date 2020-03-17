package com.github.balashov.fitness.reporter

import com.github.balashov.fitness.model.UserReport

interface ReporterQueryDao {
    fun getUserReport(userId: Int): UserReport?
}
