package com.github.balashov.exchange.exchange

import io.ktor.client.HttpClient
import io.ktor.client.features.HttpTimeout
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import io.ktor.http.HttpStatusCode

class ExchangeClientImpl(private val port: Int = 13338) : ExchangeClient {
    private val client = HttpClient {
        install(HttpTimeout) {
            requestTimeoutMillis = 2000
        }
        expectSuccess = false
    }

    private val host = "http://localhost:$port"

    override suspend fun buyShares(company: String, count: Int): Int? {
        val request = "$host/buy_shares?name=$company&count=$count"
        val response = client.get<HttpResponse>(request)

        return if (response.status == HttpStatusCode.OK) {
            response.readText().toInt()
        } else {
            null
        }
    }

    override suspend fun getSharesPrice(company: String): Int? {
        val request = "$host/get_shares_price?name=$company"
        val response = client.get<HttpResponse>(request)

        return if (response.status == HttpStatusCode.OK) {
            response.readText().toInt()
        } else {
            null
        }
    }

    override suspend fun sellShares(company: String, count: Int): Int? {
        val request = "$host/sell_shares?name=$company&count=$count"
        val response = client.get<HttpResponse>(request)

        val price = response.readText().toInt()
        return if (price == -1) {
            null
        } else {
            price
        }
    }
}
