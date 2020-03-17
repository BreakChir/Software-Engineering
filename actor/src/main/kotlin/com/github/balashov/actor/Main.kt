package com.github.balashov.actor

import akka.actor.ActorSystem
import akka.actor.Props
import akka.pattern.PatternsCS
import akka.util.Timeout
import java.util.concurrent.TimeUnit
import java.util.Scanner

import com.github.balashov.actor.actor.MasterActor

fun main() {
    val scanner = Scanner(System.`in`)
    val system = ActorSystem.create("MySystem")
    var masterNumber = 0
    while (true) {
        val query = scanner.next()
        if (query == "exit") {
            break
        }

        val master = system.actorOf(
            Props.create(MasterActor::class.java, 0), "master${masterNumber++}"
        )
        val response = PatternsCS.ask(master, query, Timeout.apply(10, TimeUnit.SECONDS))
            .toCompletableFuture()
            .join()
        println(response.toString())
    }
}
