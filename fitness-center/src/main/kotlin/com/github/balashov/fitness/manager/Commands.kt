package com.github.balashov.fitness.manager

import org.joda.time.LocalDateTime

sealed class ManagerCommand

data class RegisterUserCommand(val name: String): ManagerCommand()

data class UpdateSubscriptionCommand(val userId: Int, val endTime: LocalDateTime): ManagerCommand()
