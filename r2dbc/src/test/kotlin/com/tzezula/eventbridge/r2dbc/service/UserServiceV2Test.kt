package com.tzezula.eventbridge.r2dbc.service

import com.tzezula.eventbridge.common.SubscriptionPlan
import com.tzezula.eventbridge.common.User
import com.tzezula.eventbridge.r2dbc.config.TestConfig
import com.tzezula.eventbridge.r2dbc.config.TestPostgresConfig
import com.tzezula.eventbridge.r2dbc.event.UserChangedListener
import com.tzezula.eventbridge.r2dbc.event.UserCreatedListener
import com.tzezula.eventbridge.r2dbc.model.UserEntityRepository
import com.tzezula.eventbridge.r2dbc.tx.TransactionManager
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Import
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@DataR2dbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestPostgresConfig::class, TestConfig::class)
class UserServiceV2Test(
    @Autowired private val repository: UserEntityRepository,
    @Autowired private val publisher: ApplicationEventPublisher,
    @Autowired private val userCreatedListener: UserCreatedListener,
    @Autowired private val userChangedListener: UserChangedListener,
    @Autowired private val tx: TransactionalOperator,
) {

    private lateinit var service: UserServiceV2

    private lateinit var existingUser: User

    @BeforeEach
    fun beforeEach() = runBlocking {
        val transactionManager = TransactionManager(repository, publisher, tx)
        service = UserServiceV2(repository, transactionManager)
        existingUser = service.create("Test", SubscriptionPlan.FREE)
        userCreatedListener.reset()
    }

    @Test
    fun `should publish event after commit`(): Unit = runTest {
        // given
        val name = "John Doe"
        val plan = SubscriptionPlan.PRO

        // when
        val user = service.create(name, plan)

        // then
        val userId = user.id ?: error("User ID should not be null")
        assertEquals(name, user.name, "User name mismatch")
        assertEquals(plan, user.plan, "User plan mismatch")

        // Check if the event was published
        assertTrue(userCreatedListener.hasEvent(userId), "UserCreatedEvent not published")
    }

    @Test
    fun `should not publish event after rollback`(): Unit = runTest {
        assertThrows<IllegalStateException> {
            tx.executeAndAwait {
                // given
                val name = "John Doe"
                val plan = SubscriptionPlan.PRO

                // when
                service.create(name, plan)

                // then
                throw IllegalStateException("Simulated rollback")
            }
        }
        assertFalse(userCreatedListener.hasEvents(), "UserCreatedEvent should not be published")
    }

    @Test
    fun `should publish event after change`(): Unit = runTest {
        // given
        val userId = existingUser.id!!

        // when
        val updatedUser = service.changePlan(userId, SubscriptionPlan.PRO)

        // then
        val publishedEvent = userChangedListener.events().firstOrNull() ?: error("UserChangedEvent not published")
        assertEquals(existingUser, publishedEvent.oldUser, "Old user mismatch")
        assertEquals(updatedUser, publishedEvent.newUser, "New user mismatch")
    }
}
