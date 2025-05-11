package com.tzezula.eventbridge.r2dbc.event

import com.tzezula.eventbridge.common.UserChangedEvent
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class UserChangedListener {
    private val logger = LoggerFactory.getLogger(this::class.java)

    private val receivedEvents = mutableListOf<UserChangedEvent>()

    @EventListener
    fun handle(event: UserChangedEvent) {
        logger.info("◼ [Listener] running on thread = {}", Thread.currentThread().name)
        logger.info("◼ [Listener] received event: {}", event)
        receivedEvents.add(event)
    }

    fun events(): List<UserChangedEvent> {
        return receivedEvents.toList()
    }
}
