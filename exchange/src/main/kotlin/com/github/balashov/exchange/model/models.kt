package com.github.balashov.exchange.model

data class Share(val company: String, val price: Int, val count: Int)

data class User(val id: Int, val name: String, val money: Int, val shares: Map<String, Int>)
