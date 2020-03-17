package com.github.balashov.actor.model

interface SearchClient {
    fun search(query: String): AggregatorResponse
}
