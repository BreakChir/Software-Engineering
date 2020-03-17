package com.github.balashov.actor

import akka.actor.ActorSystem
import akka.actor.Props
import akka.pattern.PatternsCS
import akka.util.Timeout
import org.junit.Assert.*
import org.junit.AfterClass
import org.junit.Test
import java.util.concurrent.TimeUnit

import com.github.balashov.actor.actor.ChildActor
import com.github.balashov.actor.model.AggregatorResponse
import com.github.balashov.actor.server.StubServer

class ChildActorTest {

    @Test
    fun testChildActor() {
        val responseNumber = 15
        val childActor = system.actorOf(
            Props.create(
                ChildActor::class.java,
                StubServer("google", responseNumber, 0)
            )
        )
        val response: AggregatorResponse = PatternsCS.ask(
            childActor,
            "query",
            Timeout.apply(10, TimeUnit.SECONDS)
        ).toCompletableFuture().join() as AggregatorResponse

        println(response)

        assertEquals("google", response.aggregator)
        assertEquals(responseNumber, response.responses.size)
    }

    companion object {
        private val system: ActorSystem = ActorSystem.create("ChildActorTest")

        @AfterClass
        fun terminate() {
            system.terminate()
        }
    }
}