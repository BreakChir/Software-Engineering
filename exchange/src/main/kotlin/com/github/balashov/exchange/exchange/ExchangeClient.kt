package com.github.balashov.exchange.exchange

interface ExchangeClient {
    suspend fun buyShares(company: String, count: Int): Int?
    suspend fun getSharesPrice(company: String): Int?
    suspend fun sellShares(company: String, count: Int): Int?
}
