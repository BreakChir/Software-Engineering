package com.github.balashov.fitness.manager

import com.github.balashov.fitness.model.User

interface ManagerQueryDao {
    suspend fun getUser(userId: Int): User?
}
