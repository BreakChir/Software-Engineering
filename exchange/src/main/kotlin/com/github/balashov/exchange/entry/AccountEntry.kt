package com.github.balashov.exchange.entry

import com.github.balashov.exchange.account.AccountImpl
import com.github.balashov.exchange.exchange.ExchangeClientImpl
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
    val exchangeClient = ExchangeClientImpl()
    val account = AccountImpl(exchangeClient)
    embeddedServer(Netty, port = 13338) {
        routing {
            get("/register") {
                val name = call.request.queryParameters.getString("name")

                val id = account.registerUser(name)
                call.respondText("$name is registered with id $id.", status = HttpStatusCode.OK)
            }
            get("/add_money") {
                val id = call.request.queryParameters.getInt("id")
                val money = call.request.queryParameters.getInt("money")

                val isSuccessful = account.addMoney(id, money)
                if (isSuccessful) {
                    call.respondText("Success!", status = HttpStatusCode.OK)
                } else {
                    call.respondText("Something went wrong :(", status = HttpStatusCode.NotAcceptable)
                }
            }
            get("/get_money") {
                val id = call.request.queryParameters.getInt("id")

                val money = account.getMoney(id)
                if (money !== null) {
                    call.respondText("User with id $id has $money dollars", status = HttpStatusCode.OK)
                } else {
                    call.respondText("Something went wrong :(", status = HttpStatusCode.NotAcceptable)
                }
            }
            get("/get_shares") {
                val id = call.request.queryParameters.getInt("id")

                val shares = account.getShares(id)
                if (shares !== null) {
                    call.respondText("$shares", status = HttpStatusCode.OK)
                } else {
                    call.respondText("Something went wrong :(", status = HttpStatusCode.NotAcceptable)
                }
            }
            get("/get_money_with_shares") {
                val id = call.request.queryParameters.getInt("id")

                val money = account.getMoneyWithShare(id)
                if (money !== null) {
                    call.respondText("User with id $id has $money dollars", status = HttpStatusCode.OK)
                } else {
                    call.respondText("Something went wrong :(", status = HttpStatusCode.NotAcceptable)
                }
            }
            get("/buy_shares") {
                val id = call.request.queryParameters.getInt("id")
                val company = call.request.queryParameters.getString("company")
                val count = call.request.queryParameters.getInt("count")

                val result = account.buyShares(id, company, count)
                if (result !== null) {
                    call.respondText(
                        "User with id $id bought ${result.first} shares of ${result.second}",
                        status = HttpStatusCode.OK
                    )
                } else {
                    call.respondText("Couldn't buy shares", status = HttpStatusCode.NotAcceptable)
                }
            }
            get("/sell_shares") {
                val id = call.request.queryParameters.getInt("id")
                val company = call.request.queryParameters.getString("company")
                val count = call.request.queryParameters.getInt("count")

                val money = account.sellShares(id, company, count)
                if (money !== null) {
                    call.respondText("Successfully sold. Current money: $money", status = HttpStatusCode.OK)
                } else {
                    call.respondText("Couldn't sell shares", status = HttpStatusCode.NotAcceptable)
                }
            }
        }
    }.start(wait = true)
    Unit
}
