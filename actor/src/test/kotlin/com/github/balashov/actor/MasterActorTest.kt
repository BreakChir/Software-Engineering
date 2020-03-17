package com.github.balashov.actor

import akka.actor.ActorSystem
import akka.actor.Props
import akka.pattern.PatternsCS
import akka.util.Timeout
import com.github.balashov.actor.actor.MasterActor
import com.github.balashov.actor.model.AggregatorResponse
import org.junit.AfterClass
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.TimeUnit

class MasterActorTest {

    @Test
    fun testMasterActor() {
        val masterActor = system.actorOf(Props.create(MasterActor::class.java))
        val response = PatternsCS.ask(
            masterActor,
            "query",
            Timeout.apply(10, TimeUnit.SECONDS)
        ).toCompletableFuture().join() as List<AggregatorResponse>

        val aggregatorCount = 3
        assertEquals(aggregatorCount.toLong(), response.size.toLong())
        for (i in 0 until aggregatorCount) {
            assertTrue(MasterActor.AGGREGATORS.contains(response[i].aggregator))
            assertEquals(MasterActor.RESPONSE_NUMBER, response[i].responses.size)
        }
    }

    @Test
    fun testMasterActorTimeout() {
        val delay = 9
        val masterActor = system.actorOf(Props.create(MasterActor::class.java, delay))
        val response = PatternsCS.ask(
            masterActor,
            "query",
            Timeout.apply(10, TimeUnit.SECONDS)
        ).toCompletableFuture().join() as List<AggregatorResponse>

        assertTrue(response.isEmpty())
    }

    companion object {
        private val system: ActorSystem = ActorSystem.create("MasterActorTest")

        @AfterClass
        fun terminate() {
            system.terminate()
        }
    }
}