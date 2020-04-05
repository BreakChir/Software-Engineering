package com.github.balashov.exchange.exchange

import com.github.balashov.exchange.model.Share

interface Exchange {
    fun registerCompany(name: String): Boolean
    fun registerCompany(name: String, shares: Share): Boolean
    fun addShares(name: String, count: Int): Boolean
    fun getShares(name: String): Share?
    fun buyShares(name: String, count: Int): Int?
    fun changeSharePrice(name: String, newPrice: Int): Boolean
    fun sellShares(name: String, count: Int): Int
}
