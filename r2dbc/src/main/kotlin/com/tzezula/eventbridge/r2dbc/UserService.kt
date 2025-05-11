package com.tzezula.eventbridge.r2dbc

import com.tzezula.eventbridge.common.SubscriptionPlan
import com.tzezula.eventbridge.common.User
import com.tzezula.eventbridge.common.UserCreatedEvent
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait

@Service
class UserService(
    private val repository: UserEntityRepository,
    private val publisher: ApplicationEventPublisher,
    private val tx: TransactionalOperator,
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    suspend fun create(name: String, plan: SubscriptionPlan): User {
        logger.info("◼ [Service] running on thread = {}", Thread.currentThread().name)

        // Save the user entity to the database
        val entity = tx.executeAndAwait {
            val savedEntity = repository.save(UserEntity(name = name, plan = plan.name))
            registerAfterCommit(logger) {
                // Publish the UserCreatedEvent after the entity is saved
                publisher.publishEvent(
                    UserCreatedEvent(
                        user = User(
                            id = savedEntity.id,
                            name = savedEntity.name,
                            plan = SubscriptionPlan.valueOf(savedEntity.plan.uppercase())
                        )
                    )
                )
            }
            savedEntity
        }

        // Return the User object
        return User(
            id = entity.id,
            name = entity.name,
            plan = plan,
        )
    }
}
