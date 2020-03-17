package com.github.balashov.rx

import com.mongodb.rx.client.MongoClients
import io.reactivex.netty.protocol.http.server.HttpServer

fun main() {
    val router = Router(createMongoDB())

    HttpServer.newServer(8090)
        .start { request, response ->
            val resp = router.route(request)
            response.writeString(resp)
        }
        .awaitShutdown()
}

fun createMongoDB(): MongoDB {
    val client = MongoClients.create()
    val database = client.getDatabase("catalog")
    val usersCollection = database.getCollection("users")
    val productsCollection = database.getCollection("products")
    return MongoDB(usersCollection, productsCollection)
}