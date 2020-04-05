package com.github.balashov.exchange.utils

import io.ktor.application.ApplicationCall
import io.ktor.http.Parameters

fun Parameters.getString(param: String): String {
    return this[param] ?: "DefaultString"
}

fun Parameters.getInt(param: String): Int {
    return this[param]?.toInt() ?: 0
}
