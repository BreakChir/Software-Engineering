package com.github.balashov.actor.model

class SearchResponse(private val url: String, private val title: String) {
    override fun toString(): String {
        return "$url: $title"
    }
}
