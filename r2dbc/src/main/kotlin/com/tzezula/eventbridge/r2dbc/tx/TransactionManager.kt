package com.tzezula.eventbridge.r2dbc.tx

import com.tzezula.eventbridge.common.UserChangedEvent
import com.tzezula.eventbridge.common.UserCreatedEvent
import com.tzezula.eventbridge.r2dbc.model.UserEntity
import com.tzezula.eventbridge.r2dbc.model.UserEntityRepository
import com.tzezula.eventbridge.r2dbc.model.toModel
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import org.springframework.transaction.ReactiveTransaction
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait

@Component
class TransactionManager(
    private val repository: UserEntityRepository,
    private val publisher: ApplicationEventPublisher,
    private val tx: TransactionalOperator,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    suspend fun executeAndAwait(id: Long? = null, block: suspend (ReactiveTransaction) -> UserEntity): UserEntity {
        return tx.executeAndAwait { transaction ->
            val oldState = id?.let { repository.findById(it) }
            val newState = block(transaction)
            registerAfterCommit(logger) {
                when (oldState) {
                    null -> {
                        logger.info("◼ [TransactionManager] No previous state found for id: $id")
                        publisher.publishEvent(
                            UserCreatedEvent(newState.toModel())
                        )
                    }

                    else -> {
                        logger.info("◼ [TransactionManager] Previous state found for id: $id")
                        if (oldState.plan != newState.plan) {
                            logger.info("◼ [TransactionManager] Plan changed from ${oldState.plan} to ${newState.plan}")
                            publisher.publishEvent(
                                UserChangedEvent(
                                    oldUser = oldState.toModel(),
                                    newUser = newState.toModel()
                                )
                            )
                        } else {
                            logger.info("◼ [TransactionManager] No plan change detected for id: $id")
                        }
                    }
                }
            }
            newState
        }

    }
}
