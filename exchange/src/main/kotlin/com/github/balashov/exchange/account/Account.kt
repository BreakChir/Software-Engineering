package com.github.balashov.exchange.account

import com.github.balashov.exchange.model.Share

interface Account {
    fun registerUser(name: String): Int
    fun addMoney(id: Int, money: Int): Boolean
    fun getMoney(id: Int): Int?
    suspend fun getShares(id: Int): Set<Share>?
    suspend fun getMoneyWithShare(id: Int): Int?
    suspend fun buyShares(id: Int, company: String, count: Int): Pair<Int, Int>?
    suspend fun sellShares(id: Int, company: String, count: Int): Int?
}
