package com.github.balashov.exchange.entry

import com.github.balashov.exchange.exchange.ExchangeImpl
import com.github.balashov.exchange.model.Share
import com.github.balashov.exchange.utils.getInt
import com.github.balashov.exchange.utils.getString

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

import kotlinx.coroutines.runBlocking

fun main(): Unit = runBlocking {
    val exchange = ExchangeImpl()
    embeddedServer(Netty, port = 8080) {
        routing {
            get("/register") {
                val name = call.request.queryParameters.getString("name")
                val count = call.request.queryParameters.getInt("count")
                val price = call.request.queryParameters.getInt("price")

                val isSuccessful = exchange.registerCompany(name, Share(name, price, count))
                if (isSuccessful) {
                    call.respondText("$name is registered. It has $count shares with price $price")
                } else {
                    call.respondText("$name is not registered. Check your parameters.")
                }

            }
            get("/add_shares") {
                val name = call.request.queryParameters.getString("name")
                val count = call.request.queryParameters.getInt("count")

                val isSuccessful = exchange.addShares(name, count)
                if (isSuccessful) {
                    call.respondText("$count shares is added.")
                } else {
                    call.respondText("Shares is not added. Check your parameters.")
                }
            }
            get("/get_shares") {
                val name = call.request.queryParameters.getString("name")

                val shares = exchange.getShares(name)
                if (shares !== null) {
                    call.respondText("$name shares: { $shares }.")
                } else {
                    call.respondText("$name doesn't exists.")
                }
            }
            get("/get_shares_price") {
                val name = call.request.queryParameters.getString("name")

                val shares = exchange.getShares(name)
                if (shares !== null) {
                    call.respondText("${shares.price}", status = HttpStatusCode.OK)
                } else {
                    call.respondText("$name doesn't exists.", status = HttpStatusCode.NotAcceptable)
                }
            }
            get("/buy_shares") {
                val name = call.request.queryParameters.getString("name")
                val shareCount = call.request.queryParameters.getInt("count")

                val boughtShareCount = exchange.buyShares(name, shareCount)
                if (boughtShareCount !== null) {
                    call.respondText("$boughtShareCount", status = HttpStatusCode.OK)
                } else {
                    call.respondText("Couldn't buy $shareCount $name shares.", status = HttpStatusCode.NotAcceptable)
                }
            }
            get("/change_share_price") {
                val name = call.request.queryParameters.getString("name")
                val newPrice = call.request.queryParameters.getInt("price")

                val isSuccessful = exchange.changeSharePrice(name, newPrice)
                if (isSuccessful) {
                    call.respondText("$name shares changes price to $newPrice.")
                } else {
                    call.respondText("Couldn't change $name shares price.")
                }
            }
            get("/sell_shares") {
                val name = call.request.queryParameters.getString("name")
                val count = call.request.queryParameters.getInt("count")

                val price = exchange.sellShares(name, count)
                if (price >= 0) {
                    call.respondText("$price", status = HttpStatusCode.OK)
                } else {
                    call.respondText("$price", status = HttpStatusCode.NotAcceptable)
                }
            }
        }
    }.start(wait = true)
    Unit
}
