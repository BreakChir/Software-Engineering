package com.github.balashov.fitness.turnstile

import com.github.balashov.fitness.utils.Time
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.joda.time.LocalDateTime

class TurnstileCommandService(private val turnstile: TurnstileCommandDao) {
    private suspend fun sendVisit(userId: Int, startTime: LocalDateTime, endTime: LocalDateTime, eventId: Int): String {
        val startStr = Time.timeToString(startTime)
        val endStr = Time.timeToString(endTime)
        val url = "http://localhost:13338/command/add_visit?" +
                "user_id=$userId&start_time=$startStr&end_time=$endStr&event_id=$eventId"
        return HttpClient().get(url)
    }

    suspend fun process(task: TurnstileCommand): String = try {
        when (task) {
            is EnterCommand -> {
                turnstile.processEnter(task.userId, task.time)
                "Entering..."
            }
            is ExitCommand -> {
                val (startTime, eventId) = turnstile.processExit(task.userId, task.time)
                val request = "user_id = ${task.userId}, start_time = $startTime" +
                        ", end_time = ${task.time}, event_id = $eventId"
                GlobalScope.launch {
                    val response = try {
                        sendVisit(task.userId, startTime, task.time, eventId)
                    } catch (e: Exception) {
                        "ERROR: ${e.message}"
                    }
                    System.err.println("$request: $response")
                }
                "Exiting..."
            }
        }
    } catch (e: Exception) {
        "Error while processing: ${e.message}"
    }
}
