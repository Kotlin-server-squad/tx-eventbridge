package com.tzezula.eventbridge.jdbc

import com.tzezula.eventbridge.common.UserCreatedEvent
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class UserCreatedListener {
    private val logger = LoggerFactory.getLogger(this::class.java)

    private val receivedEvents = mutableListOf<UserCreatedEvent>()

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handle(event: UserCreatedEvent) {
        logger.info("◼ [Listener] running on thread = {}", Thread.currentThread().name)
        logger.info("◼ [Listener] received event: {}", event)
        receivedEvents.add(event)
    }

    fun hasEvent(userId: Long): Boolean {
        return receivedEvents.any { it.userId == userId }
    }
}
