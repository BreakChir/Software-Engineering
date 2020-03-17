package com.github.balashov.fitness.manager

import com.github.jasync.sql.db.SuspendingConnection

import com.github.balashov.fitness.database.DatabaseAbstract
import com.github.balashov.fitness.model.User

class ManagerQueryDaoImpl(
    private val connection: SuspendingConnection
) : ManagerQueryDao, DatabaseAbstract() {

    override suspend fun getUser(userId: Int): User? =
        connection.inTransaction { getUser(it, userId).first }
}
