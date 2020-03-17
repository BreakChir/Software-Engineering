package com.github.balashov.fitness.database

import com.github.jasync.sql.db.SuspendingConnection
import com.github.jasync.sql.db.asSuspending
import com.github.jasync.sql.db.postgresql.PostgreSQLConnectionBuilder

object DatabaseConnection {
    fun createSuspendingConnection(): SuspendingConnection {
        return PostgreSQLConnectionBuilder.createConnectionPool {
            host = "localhost"
            database = "fitness"
            username = "postgres"
            port = 5432
        }.asSuspending
    }
}
