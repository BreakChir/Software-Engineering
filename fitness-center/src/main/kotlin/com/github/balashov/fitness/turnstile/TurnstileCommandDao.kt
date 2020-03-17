package com.github.balashov.fitness.turnstile

import org.joda.time.LocalDateTime

interface TurnstileCommandDao {
    suspend fun processEnter(userId: Int, time: LocalDateTime)

    suspend fun processExit(userId: Int, time: LocalDateTime): Pair<LocalDateTime, Int>
}
