package com.github.balashov.fitness.manager

import org.joda.time.LocalDateTime

interface ManagerCommandDao {
    suspend fun registerUser(name: String): Int

    suspend fun updateSubscription(userId: Int, endTime: LocalDateTime)
}
