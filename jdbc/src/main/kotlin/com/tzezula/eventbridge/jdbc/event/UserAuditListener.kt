package com.tzezula.eventbridge.jdbc.event

import com.tzezula.eventbridge.common.UserChangedEvent
import com.tzezula.eventbridge.common.UserCreatedEvent
import com.tzezula.eventbridge.jdbc.model.UserEntityV2
import com.tzezula.eventbridge.jdbc.model.toModel
import jakarta.persistence.PostPersist
import jakarta.persistence.PostUpdate
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class UserAuditListener(
    private val publisher: ApplicationEventPublisher,
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @PostPersist
    @PostUpdate
    fun onSave(entity: UserEntityV2) {
        // Old snapshot was captured in @PostLoad
        val event = when (val oldState = entity.beforeState()) {
            null -> {
                logger.info("Sending notification about new user entity {}", entity.id)
                UserCreatedEvent(entity.toModel())
            }
            else -> {
                logger.info("User entity {} was modified", entity.id)
                UserChangedEvent(
                    oldUser = oldState.toModel(),
                    newUser = entity.toModel()
                )
            }
        }
        publisher.publishEvent(event)
    }
}
