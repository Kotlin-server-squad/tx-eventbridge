package com.tzezula.eventbridge.jdbc.event

import com.tzezula.eventbridge.common.UserChangedEvent
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class UserChangedListener {
    private val logger = LoggerFactory.getLogger(this::class.java)

    private val receivedEvents = mutableListOf<UserChangedEvent>()

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handle(event: UserChangedEvent) {
        logger.info("◼ [Listener] received event: {}", event)
        receivedEvents.add(event)
    }

    fun events(): List<UserChangedEvent> {
        return receivedEvents.toList()
    }
}
