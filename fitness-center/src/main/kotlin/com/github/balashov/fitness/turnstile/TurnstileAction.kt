package com.github.balashov.fitness.turnstile

import org.joda.time.LocalDateTime

data class TurnstileAction(val type: TurnstileActionType, val time: LocalDateTime)
