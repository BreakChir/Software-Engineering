package com.github.balashov.fitness.model

import org.joda.time.Period

data class UserReport(val totalVisits: Int, val totalTimeSpent: Period) {
    override fun equals(other: Any?): Boolean {
        if (other == null || other !is UserReport) {
            return false
        }
        return totalVisits == other.totalVisits &&
                totalTimeSpent.normalizedStandard() == other.totalTimeSpent.normalizedStandard()
    }

    override fun hashCode(): Int {
        var result = totalVisits
        result = 31 * result + totalTimeSpent.hashCode()
        return result
    }
}
