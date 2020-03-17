package com.github.balashov.fitness.reporter

import com.github.balashov.fitness.sql.SQLQuery
import com.github.jasync.sql.db.QueryResult
import com.github.jasync.sql.db.ResultSet
import com.github.jasync.sql.db.RowData
import com.github.jasync.sql.db.SuspendingConnection
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import org.joda.time.Period

fun mockkInitWithEmptyData(connection: SuspendingConnection) {
    val rows = mockk<ResultSet>()
    val rowDataIterator = mockk<Iterator<RowData>>()
    every { rowDataIterator.hasNext() }.returns(false)
    every { rows.iterator() }.returns(rowDataIterator)

    coEvery {
        connection.sendPreparedStatement(SQLQuery.getUserReports)
    }.returns(QueryResult(0, "OK", rows))
}

fun mockkInitNotEmptyData(
    connection: SuspendingConnection,
    userId: Int,
    totalVisits: Long,
    totalTime: Period,
    maxExitId: Int
) {
    val rows = mockk<ResultSet>()
    val rowDataIterator = mockk<Iterator<RowData>>()
    val row = mockk<RowData>()

    every { row.getInt("userId") }.returns(userId)
    every { row.getLong("totalVisits") }.returns(totalVisits)
    every { row.getAs<Period>("totalTime") }.returns(totalTime)
    every { row.getInt("maxExitId") }.returns(maxExitId)

    every { rowDataIterator.hasNext() }.returns(true).andThen(false)
    every { rowDataIterator.next() }.returns(row)
    every { rows.iterator() }.returns(rowDataIterator)

    coEvery {
        connection.sendPreparedStatement(SQLQuery.getUserReports)
    }.returns(QueryResult(0, "OK", rows))
}
