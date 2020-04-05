package com.github.balashov.exchange.exchange

import com.github.balashov.exchange.model.Share

class ExchangeImpl : Exchange {
    private val companyShares = HashMap<String, Share>()

    override fun registerCompany(name: String): Boolean {
        return registerCompany(name, Share(name, 0, 0))
    }

    override fun registerCompany(name: String, shares: Share): Boolean {
        if (shares.price < 0 || shares.count < 0 || companyShares.contains(name)) {
            return false
        }

        companyShares[name] = shares
        return true
    }

    override fun addShares(name: String, count: Int): Boolean {
        if (!companyShares.contains(name) || count < 0) {
            return false
        }

        companyShares[name] = Share(name, companyShares[name]!!.price, companyShares[name]!!.count)
        return true
    }

    override fun getShares(name: String): Share? {
        return companyShares[name]
    }

    override fun buyShares(name: String, count: Int): Int? {
        if (!companyShares.contains(name) || count < 0) {
            return null
        }

        val boughtShareCount = count.coerceAtMost(companyShares[name]!!.count)
        val price = companyShares[name]!!.price
        companyShares[name] = Share(name, price, companyShares[name]!!.count - boughtShareCount)
        return boughtShareCount
    }

    override fun changeSharePrice(name: String, newPrice: Int): Boolean {
        if (!companyShares.contains(name) || newPrice < 0) {
            return false
        }

        companyShares[name] = Share(name, newPrice, companyShares[name]!!.count)
        return true
    }

    override fun sellShares(name: String, count: Int): Int {
        if (!companyShares.contains(name) || count < 0) {
            return -1
        }

        companyShares[name] = Share(name, companyShares[name]!!.price, companyShares[name]!!.count + count)
        return companyShares[name]!!.price
    }
}
