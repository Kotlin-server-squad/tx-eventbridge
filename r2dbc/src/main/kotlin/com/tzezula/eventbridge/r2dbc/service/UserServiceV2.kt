package com.tzezula.eventbridge.r2dbc.service

import com.tzezula.eventbridge.common.SubscriptionPlan
import com.tzezula.eventbridge.common.User
import com.tzezula.eventbridge.r2dbc.tx.TransactionManager
import com.tzezula.eventbridge.r2dbc.model.UserEntity
import com.tzezula.eventbridge.r2dbc.model.UserEntityRepository
import com.tzezula.eventbridge.r2dbc.model.toModel
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class UserServiceV2(
    private val repository: UserEntityRepository,
    private val tx: TransactionManager,
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    suspend fun create(name: String, plan: SubscriptionPlan): User {
        logger.info("◼ [Service] running on thread = {}", Thread.currentThread().name)

        // Save the user entity to the database
        val entity = tx.executeAndAwait {
            repository.save(UserEntity(name = name, plan = plan.name))
        }

        // Return the User object
        return entity.toModel()
    }

    suspend fun changePlan(userId: Long, newPlan: SubscriptionPlan): User {
        logger.info("◼ [Service] changing plan for userId {} to {}", userId, newPlan)

        // Find the user entity by ID
        val entity = repository.findById(userId)
            ?: throw IllegalArgumentException("User with ID $userId not found")

        // Update the subscription plan
        entity.plan = newPlan.name

        // Save the updated entity to the database
        val updatedEntity = tx.executeAndAwait(id = userId) {
            repository.save(entity)
        }

        // Return the updated User object
        return updatedEntity.toModel()
    }
}
