package com.github.balashov.fitness.manager

class ManagerCommandService(private val manager: ManagerCommandDao) {
    suspend fun process(task: ManagerCommand): String = try {
        when (task) {
            is RegisterUserCommand -> {
                val id = manager.registerUser(task.name)
                "User ${task.name} has id = $id"
            }
            is UpdateSubscriptionCommand -> {
                manager.updateSubscription(task.userId, task.endTime)
                "Successfully update subscription"
            }
        }
    } catch (e: Exception) {
        "Error while processing: ${e.message}"
    }
}
