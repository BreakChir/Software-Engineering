package com.github.balashov.fitness.utils

import org.joda.time.LocalDateTime

object Time {
    fun timeToString(time: LocalDateTime): String = time.toString("yyyy-MM-dd'T'HH:mm:ss")
}
