package com.github.balashov.fitness.entry

import io.ktor.application.call
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.runBlocking

import com.github.balashov.fitness.database.DatabaseConnection
import com.github.balashov.fitness.reporter.*
import com.github.balashov.fitness.utils.getInt
import com.github.balashov.fitness.utils.getTime

fun main(): Unit = runBlocking {
    val connection = DatabaseConnection.createSuspendingConnection()
    val reportStore = ReportStore(connection)
    reportStore.init()
    val reporterCommand = ReporterCommandDaoImpl(reportStore)
    val reporterCommandService = ReportCommandService(reporterCommand)
    val reporterQuery = ReporterQueryDaoImpl(reportStore)
    val reporterQueryService = ReportQueryService(reporterQuery)

    embeddedServer(Netty, port = 13338) {
        routing {
            get("/report") {
                val userId = call.request.queryParameters.getInt("user_id")
                val query = GetUserReportQuery(userId)
                call.respondText(reporterQueryService.process(query))
            }
            get("/visit") {
                val userId = call.request.queryParameters.getInt("user_id")
                val eventId = call.request.queryParameters.getInt("event_id")
                val startTime = call.request.queryParameters.getTime("start_time")
                val endTime = call.request.queryParameters.getTime("end_time")
                val command = AddVisitCommand(userId, startTime, endTime, eventId)
                call.respondText(reporterCommandService.process(command))
            }
        }
    }.start(wait = true)
    Unit
}
