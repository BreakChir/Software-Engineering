package com.github.balashov.fitness.entry

import io.ktor.application.call
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

import kotlinx.coroutines.runBlocking

import com.github.balashov.fitness.database.DatabaseConnection
import com.github.balashov.fitness.manager.*
import com.github.balashov.fitness.manager.GetUserQuery
import com.github.balashov.fitness.utils.getInt
import com.github.balashov.fitness.utils.getString
import com.github.balashov.fitness.utils.getTime

fun main(): Unit = runBlocking {
    val connection = DatabaseConnection.createSuspendingConnection()
    val managerCommand = ManagerCommandDaoImpl(connection)
    val managerCommandService = ManagerCommandService(managerCommand)
    val managerQuery = ManagerQueryDaoImpl(connection)
    val managerQueryService = ManagerQueryService(managerQuery)
    embeddedServer(Netty, port = 13337) {
        routing {
            get("/register") {
                val name = call.request.queryParameters.getString("name")
                val command = RegisterUserCommand(name)
                call.respondText(managerCommandService.process(command))
            }
            get("/update") {
                val userId = call.request.queryParameters.getInt("user_id")
                val endTime = call.request.queryParameters.getTime("end_time")
                val command = UpdateSubscriptionCommand(userId, endTime)
                call.respondText(managerCommandService.process(command))
            }
            get("/user") {
                val userId = call.request.queryParameters.getInt("user_id")
                val query = GetUserQuery(userId)
                call.respondText(managerQueryService.process(query))
            }
        }
    }.start(wait = true)
    Unit
}
