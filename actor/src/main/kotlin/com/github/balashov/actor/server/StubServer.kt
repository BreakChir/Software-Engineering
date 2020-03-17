package com.github.balashov.actor.server

import com.github.balashov.actor.model.AggregatorResponse
import com.github.balashov.actor.model.SearchClient
import com.github.balashov.actor.model.SearchResponse

class StubServer constructor(
    private val aggregator: String,
    private val responseNumber: Int,
    private val sleep: Int = 0
) : SearchClient {

    override fun search(query: String): AggregatorResponse {
        val responses: MutableList<SearchResponse> = ArrayList()
        try {
            Thread.sleep(sleep * 1000.toLong())
            for (i in 0 until responseNumber) {
                responses.add(
                    SearchResponse(
                        generateUrl(i, query),
                        generateTitle(i)
                    )
                )
            }
        } catch (ignored: InterruptedException) {
        }
        return AggregatorResponse(aggregator, responses)
    }

    private fun generateHef(index: Int): String {
        return "http://${aggregator.toLowerCase()}_url_$index/"
    }

    private fun generateCGI(): String {
        val cgiCount = randInt(1, 3)
        val builder = StringBuilder("?")
        for (i in 0 until cgiCount) {
            builder.append("cgi").append(i).append("=").append(i)
            if (i < cgiCount - 1) builder.append('_')
        }
        return builder.toString()
    }

    private fun generateUrl(index: Int, query: String): String {
        return generateHef(index) + query + generateCGI()
    }

    private fun generateTitle(index: Int): String {
        return "Title$index"
    }

    companion object {
        fun randInt(min: Int, max: Int): Int {
            return min + (Math.random() * (max - min + 1)).toInt()
        }
    }
}
