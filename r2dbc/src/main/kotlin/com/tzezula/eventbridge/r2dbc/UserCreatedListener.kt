package com.tzezula.eventbridge.r2dbc

import com.tzezula.eventbridge.common.UserCreatedEvent
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class UserCreatedListener {
    private val logger = LoggerFactory.getLogger(this::class.java)

    private val receivedEvents = mutableListOf<UserCreatedEvent>()

    @EventListener
    fun handle(event: UserCreatedEvent) {
        logger.info("◼ [Listener] running on thread = {}", Thread.currentThread().name)
        logger.info("◼ [Listener] received event: {}", event)
        receivedEvents.add(event)
    }

    fun hasEvent(userId: Long): Boolean {
        return receivedEvents.any { it.userId == userId }
    }

    fun hasEvents(): Boolean {
        return receivedEvents.isNotEmpty()
    }

    fun reset() {
        receivedEvents.clear()
    }
}
