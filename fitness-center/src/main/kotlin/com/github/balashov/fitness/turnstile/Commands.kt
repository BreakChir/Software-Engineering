package com.github.balashov.fitness.turnstile

import org.joda.time.LocalDateTime

sealed class TurnstileCommand

data class EnterCommand(val userId: Int, val time: LocalDateTime) : TurnstileCommand()

data class ExitCommand(val userId: Int, val time: LocalDateTime) : TurnstileCommand()
