package com.github.balashov.actor.actor

import akka.actor.UntypedAbstractActor
import com.github.balashov.actor.model.SearchClient

class ChildActor(private val searchClient: SearchClient) : UntypedAbstractActor() {

    override fun onReceive(message: Any) {
        if (message is String) {
            sender.tell(searchClient.search(message), self())
            context.stop(self())
        }
    }
}
