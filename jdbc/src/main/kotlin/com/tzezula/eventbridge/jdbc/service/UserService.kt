package com.tzezula.eventbridge.jdbc.service

import com.tzezula.eventbridge.common.SubscriptionPlan
import com.tzezula.eventbridge.common.User
import com.tzezula.eventbridge.common.UserCreatedEvent
import com.tzezula.eventbridge.jdbc.model.UserEntity
import com.tzezula.eventbridge.jdbc.model.UserEntityRepository
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val repository: UserEntityRepository,
    private val publisher: ApplicationEventPublisher,
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Transactional
    fun create(name: String, plan: SubscriptionPlan): User {

        logger.info("◼ [Service] running on thread = {}", Thread.currentThread().name)

        // Save the user entity to the database
        val entity = repository.save(UserEntity(name, plan))

        val user = User(
            id = entity.id,
            name = entity.name,
            plan = entity.plan,
        )

        // Publish the UserCreatedEvent after the entity is saved
        publisher.publishEvent(UserCreatedEvent(user))

        // Return the User object
        return user
    }
}
