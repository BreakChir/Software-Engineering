package com.github.balashov.exchange.account

import com.github.balashov.exchange.exchange.ExchangeClient
import com.github.balashov.exchange.model.Share
import com.github.balashov.exchange.model.User

class AccountImpl(private val client: ExchangeClient) : Account {
    private val users = HashMap<Int, User>()

    override fun registerUser(name: String): Int {
        val id = users.size
        users[id] = User(id, name, 0, HashMap())
        return id
    }

    override fun addMoney(id: Int, money: Int): Boolean {
        if (!users.contains(id)) {
            return false
        }

        users[id] = User(id, users[id]!!.name, users[id]!!.money + money, users[id]!!.shares)
        return true
    }

    override fun getMoney(id: Int): Int? {
        if (!users.contains(id)) {
            return null
        }

        return users[id]!!.money
    }

    override suspend fun getShares(id: Int): Set<Share>? {
        if (!users.contains(id)) {
            return null
        }

        val shares = HashSet<Share>()
        for ((company, count) in users[id]!!.shares) {
            val price = client.getSharesPrice(company)
            if (price === null) continue
            shares.add(Share(company, price, count))
        }

        return shares
    }

    override suspend fun getMoneyWithShare(id: Int): Int? {
        if (!users.contains(id)) {
            return null
        }

        var sum = 0
        for ((company, count) in users[id]!!.shares) {
            val price = client.getSharesPrice(company)
            if (price === null) continue
            sum += price * count
        }

        return sum + users[id]!!.money
    }

    override suspend fun buyShares(id: Int, company: String, count: Int): Pair<Int, Int>? {
        if (!users.contains(id) || count < 0) {
            return null
        }

        val sharesPrice = client.getSharesPrice(company)
        if (sharesPrice == null || sharesPrice * count > users[id]!!.money) {
            return null
        }

        val boughtShareCount = client.buyShares(company, count) ?: return null
        val shares = users[id]!!.shares.toMutableMap()
        shares[company] = shares.getOrDefault(company, 0) + boughtShareCount

        users[id] = User(id, users[id]!!.name, users[id]!!.money - sharesPrice * boughtShareCount, shares)
        return Pair(boughtShareCount, sharesPrice)
    }

    override suspend fun sellShares(id: Int, company: String, count: Int): Int? {
        if (!users.contains(id) || !users[id]!!.shares.contains(company) || users[id]!!.shares[company]!! < count) {
            return null
        }

        val shares = users[id]!!.shares.toMutableMap()
        shares[company] = shares[company]!! - count

        val sharesMoney = client.sellShares(company, count)
        if (sharesMoney === null) return null

        users[id] = User(id, users[id]!!.name, users[id]!!.money + sharesMoney * count, shares)
        return users[id]!!.money
    }
}
