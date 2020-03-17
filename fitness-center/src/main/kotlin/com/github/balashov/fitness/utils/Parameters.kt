package com.github.balashov.fitness.utils

import io.ktor.http.Parameters
import org.joda.time.LocalDateTime

fun Parameters.getString(param: String): String {
    return this[param] ?: "DefaultString"
}

fun Parameters.getInt(param: String): Int {
    return this[param]?.toInt() ?: -1
}

fun Parameters.getTime(param: String): LocalDateTime {
    return LocalDateTime.parse(this[param] ?: "2019-01-01T00:00:00")
}
