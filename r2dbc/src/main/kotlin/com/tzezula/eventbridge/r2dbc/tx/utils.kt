package com.tzezula.eventbridge.r2dbc.tx

import kotlinx.coroutines.reactor.awaitSingle
import org.slf4j.Logger
import org.springframework.transaction.NoTransactionException
import org.springframework.transaction.reactive.TransactionSynchronization
import org.springframework.transaction.reactive.TransactionSynchronizationManager
import reactor.core.publisher.Mono

suspend fun registerAfterCommit(logger: Logger, onCommit: () -> Unit) {
    try {
        val syncManager = TransactionSynchronizationManager.forCurrentTransaction().awaitSingle()
        syncManager.registerSynchronization(object : TransactionSynchronization {
            override fun afterCommit(): Mono<Void> {
                return Mono.fromRunnable<Unit> {
                    onCommit()
                }.then()
            }
        })
    } catch (e: NoTransactionException) {
        logger.warn("No active transaction found, skipping afterCommit registration.")
    }
}
