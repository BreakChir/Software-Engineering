package com.github.balashov.fitness.entry

import io.ktor.application.call
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

import kotlinx.coroutines.runBlocking
import org.joda.time.LocalDateTime

import com.github.balashov.fitness.database.DatabaseConnection
import com.github.balashov.fitness.turnstile.EnterCommand
import com.github.balashov.fitness.turnstile.ExitCommand
import com.github.balashov.fitness.turnstile.TurnstileCommandService
import com.github.balashov.fitness.turnstile.TurnstileCommandDaoImpl
import com.github.balashov.fitness.utils.getInt

fun main(): Unit = runBlocking {
    val connection = DatabaseConnection.createSuspendingConnection()
    val turnstileCommand = TurnstileCommandDaoImpl(connection)
    val turnstileCommandService = TurnstileCommandService(turnstileCommand)
    embeddedServer(Netty, port = 13339) {
        routing {
            get("/enter") {
                val userId = call.request.queryParameters.getInt("user_id")
                val command = EnterCommand(userId, LocalDateTime.now())
                call.respondText(turnstileCommandService.process(command))
            }
            get("/exit") {
                val userId = call.request.queryParameters.getInt("user_id")
                val command = ExitCommand(userId, LocalDateTime.now())
                call.respondText(turnstileCommandService.process(command))
            }
        }
    }.start(wait = true)
    Unit
}
