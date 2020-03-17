package com.github.balashov.actor.model

class AggregatorResponse(
    val aggregator: String,
    val responses: List<SearchResponse>
) {

    override fun toString(): String {
        return "$aggregator { $responses\n}"
    }
}
