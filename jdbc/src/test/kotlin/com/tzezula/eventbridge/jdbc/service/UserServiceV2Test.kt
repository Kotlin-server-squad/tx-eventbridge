package com.tzezula.eventbridge.jdbc.service

import com.tzezula.eventbridge.common.SubscriptionPlan
import com.tzezula.eventbridge.common.User
import com.tzezula.eventbridge.jdbc.config.TestConfig
import com.tzezula.eventbridge.jdbc.config.TestPostgresConfig
import com.tzezula.eventbridge.jdbc.event.UserChangedListener
import com.tzezula.eventbridge.jdbc.event.UserCreatedListener
import com.tzezula.eventbridge.jdbc.model.UserEntityV2Repository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.transaction.TestTransaction
import org.springframework.transaction.annotation.Transactional
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestPostgresConfig::class, TestConfig::class)
class UserServiceV2Test(
    @Autowired private val repository: UserEntityV2Repository,
    @Autowired private val userCreatedListener: UserCreatedListener,
    @Autowired private val userChangedListener: UserChangedListener,
) {

    private lateinit var service: UserServiceV2

    private lateinit var existingUser: User

    @BeforeEach
    @Transactional
    fun beforeEach() {
        service = UserServiceV2(repository)
        existingUser = service.create("Test", SubscriptionPlan.FREE)
    }

    @Transactional
    fun afterEach() {
        repository.deleteAll()
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

    @Test
    fun `should publish change event`() {
        // given
        val userId = existingUser.id!!

        // when
        val updatedUser = service.changePlan(userId, SubscriptionPlan.PRO)

        // then
        assertTrue(userChangedListener.events().isEmpty(), "UserChangedEvent should not be published yet")

        TestTransaction.flagForCommit()
        TestTransaction.end()

        val publishedEvent = userChangedListener.events().firstOrNull() ?: error("UserChangedEvent not published")
        assertEquals(existingUser, publishedEvent.oldUser, "Old user mismatch")
        assertEquals(updatedUser, publishedEvent.newUser, "New user mismatch")
    }
}
