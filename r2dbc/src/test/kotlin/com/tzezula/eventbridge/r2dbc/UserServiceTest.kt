package com.tzezula.eventbridge.r2dbc

import com.tzezula.eventbridge.common.SubscriptionPlan
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
class UserServiceTest(
    @Autowired private val repository: UserEntityRepository,
    @Autowired private val publisher: ApplicationEventPublisher,
    @Autowired private val userCreatedListener: UserCreatedListener,
    @Autowired private val tx: TransactionalOperator,
) {

    private lateinit var service: UserService

    @BeforeEach
    fun beforeEach() {
        service = UserService(repository, publisher, tx)
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
}
