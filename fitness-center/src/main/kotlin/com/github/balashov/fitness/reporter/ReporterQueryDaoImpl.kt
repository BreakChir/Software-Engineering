package com.github.balashov.fitness.reporter

import com.github.balashov.fitness.model.UserReport

class ReporterQueryDaoImpl(private val state: ReportStore) : ReporterQueryDao {
    override fun getUserReport(userId: Int): UserReport? = state.getUserReport(userId)
}
