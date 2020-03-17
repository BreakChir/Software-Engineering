package com.github.balashov.fitness.sql

object SQLQuery {
    val addUserSQL =
        """
            INSERT INTO users (userId, name)
            VALUES (?, ?)
        """.trimIndent()

    val getUserSQL =
        """
            SELECT *
            FROM users LEFT JOIN subscriptionEvents USING (userId)
            WHERE userId = ?
            ORDER BY eventId DESC;
        """.trimIndent()

    val addSubscriptionSQL =
        """
            INSERT INTO subscriptionEvents (userId, eventId, endTime)
            VALUES (?, ?, ?)
        """.trimIndent()

    val getMaxIdSQL =
        """
            SELECT maxId
            FROM maxIds
            WHERE entity = 'USER';
        """.trimIndent()

    val updateMaxIdSQL =
        """
            UPDATE maxIds
            SET maxId = ?
            WHERE entity = 'USER'
              AND maxId = ?
        """.trimIndent()

    val getUserReports =
        """
            WITH rankedEvents AS (
                SELECT userId,
                    eventType,
                    eventTime,
                    eventId,
                    rank() OVER (PARTITION BY (userId, eventType) ORDER BY eventId) AS num
                FROM turnstileEvents
            ),
                exits AS (
                    SELECT userId,
                        num,
                        eventTime AS exitTime,
                        eventId   AS exitId
                    FROM rankedEvents
                    WHERE eventType = 'EXIT'
                ),
                enters AS (
                    SELECT userId,
                        num,
                        eventTime AS enterTime
                    FROM rankedEvents
                    WHERE eventType = 'ENTER'
                )
            SELECT userId,
                count(1)                  AS totalVisits,
                sum(exitTime - enterTime) AS totalTime,
                max(exitId)               AS maxExitId
            FROM exits JOIN enters USING (userId, num)
            GROUP BY userId
        """.trimIndent()

    val addTurnstileEvent =
        """
            INSERT INTO turnstileEvents (userId, eventId, eventType, eventTime)
            VALUES (?, ?, ?, ?)
        """.trimIndent()

    val getTurnstileEvents =
        """
            SELECT *
            FROM users LEFT JOIN turnstileEvents USING (userId)
            WHERE userId = ?
            ORDER BY eventId DESC
        """.trimIndent()
}
