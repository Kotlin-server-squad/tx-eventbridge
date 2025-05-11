package com.tzezula.eventbridge.jdbc.service

import com.tzezula.eventbridge.common.SubscriptionPlan
import com.tzezula.eventbridge.common.User
import com.tzezula.eventbridge.jdbc.model.UserEntityV2
import com.tzezula.eventbridge.jdbc.model.UserEntityV2Repository
import com.tzezula.eventbridge.jdbc.model.toModel
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserServiceV2(
    private val repository: UserEntityV2Repository,
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Transactional
    fun create(name: String, plan: SubscriptionPlan): User {
        logger.info("◼ [ServiceV2] running on thread = {}", Thread.currentThread().name)

        // Save the user entity to the database
        val entity = repository.save(UserEntityV2(name = name, plan = plan))

        // Return the created user as a model
        return entity.toModel()
    }

    @Transactional
    fun changePlan(userId: Long, newPlan: SubscriptionPlan): User {
        logger.info("◼ [ServiceV2] changing plan for userId {} to {}", userId, newPlan)

        // Find the user entity by ID
        val entity = repository.findById(userId).orElseThrow {
            IllegalArgumentException("User with ID $userId not found")
        }

        // Update the subscription plan
        entity.plan = newPlan

        // Save the updated entity to the database
        val updatedEntity = repository.save(entity)

        // Return the updated user as a model
        return updatedEntity.toModel()
    }
}
