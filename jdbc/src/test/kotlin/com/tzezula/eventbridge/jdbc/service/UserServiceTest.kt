package com.tzezula.eventbridge.jdbc.service

import com.tzezula.eventbridge.common.SubscriptionPlan
import com.tzezula.eventbridge.jdbc.config.TestConfig
import com.tzezula.eventbridge.jdbc.config.TestPostgresConfig
import com.tzezula.eventbridge.jdbc.event.UserCreatedListener
import com.tzezula.eventbridge.jdbc.model.UserEntityRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Import
import org.springframework.test.context.transaction.TestTransaction
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestPostgresConfig::class, TestConfig::class)
class UserServiceTest(
    @Autowired private val repository: UserEntityRepository,
    @Autowired private val publisher: ApplicationEventPublisher,
    @Autowired private val userCreatedListener: UserCreatedListener,
) {

    private lateinit var service: UserService

    @BeforeEach
    fun beforeEach() {
        service = UserService(repository, publisher)
    }

    @Test
    fun `should publish event after commit`() {
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
        assertFalse(userCreatedListener.hasEvent(userId), "UserCreatedEvent should not be published yet")

        TestTransaction.flagForCommit()
        TestTransaction.end()

        assertTrue(userCreatedListener.hasEvent(userId), "UserCreatedEvent not published")
    }

    @Test
    fun `should not publish event after rollback`() {
        // given
        val name = "John Doe"
        val plan = SubscriptionPlan.PRO

        // when
        val user = service.create(name, plan)

        // then
        val userId = user.id ?: error("User ID should not be null")
        assertEquals(name, user.name, "User name mismatch")
        assertEquals(plan, user.plan, "User plan mismatch")

        TestTransaction.flagForRollback()
        TestTransaction.end()

        assertFalse(userCreatedListener.hasEvent(userId), "UserCreatedEvent should not be published")
    }
}
