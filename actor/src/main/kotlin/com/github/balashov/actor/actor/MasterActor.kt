package com.github.balashov.actor.actor

import akka.actor.ActorRef
import akka.actor.Props
import akka.actor.ReceiveTimeout
import akka.actor.UntypedAbstractActor
import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit
import java.util.function.Consumer

import com.github.balashov.actor.model.AggregatorResponse
import com.github.balashov.actor.server.StubServer

class MasterActor(
    private val delay: Int = 0,
    private val aggregatorResponses: MutableList<AggregatorResponse> = ArrayList()
) : UntypedAbstractActor() {

    constructor(delay: Int): this(delay, ArrayList())

    private lateinit var requestSender: ActorRef

    private var childNumber = 0

    private fun getChildName() = "child${childNumber++}"

    override fun onReceive(message: Any) {
        if (message is String) {
            requestSender = sender
            AGGREGATORS.forEach(Consumer { aggregator ->
                val child = context.actorOf(
                    Props.create(
                        ChildActor::class.java,
                        StubServer(aggregator, RESPONSE_NUMBER, delay)
                    ),
                    getChildName()
                )
                child.tell(message, self())
            })
            context.setReceiveTimeout(
                Duration.create(
                    TIMEOUT,
                    TimeUnit.SECONDS
                )
            )
        } else if (message is AggregatorResponse) {
            aggregatorResponses.add(message)
            if (aggregatorResponses.size == AGGREGATORS.size) {
                requestSender.tell(aggregatorResponses, self())
                context.stop(self())
            }
        } else if (message is ReceiveTimeout) {
            requestSender.tell(aggregatorResponses, self())
            context.stop(self())
        }
    }

    companion object {
        const val RESPONSE_NUMBER = 15
        const val TIMEOUT = 1L
        val AGGREGATORS = listOf("google", "yandex", "bing")
    }
}
