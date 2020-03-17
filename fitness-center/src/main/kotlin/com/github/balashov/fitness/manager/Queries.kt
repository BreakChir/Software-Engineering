package com.github.balashov.fitness.manager

sealed class ManagerQuery

data class GetUserQuery(val userId: Int) : ManagerQuery()
