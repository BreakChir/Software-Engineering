package com.github.balashov.fitness.manager

class ManagerQueryService(private val manager: ManagerQueryDao) {
    suspend fun process(task: ManagerQuery): String = try {
        when (task) {
            is GetUserQuery -> {
                manager.getUser(task.userId)?.toString() ?: "No such user"
            }
        }
    } catch (e: Exception) {
        "Error while processing: ${e.message}"
    }
}
