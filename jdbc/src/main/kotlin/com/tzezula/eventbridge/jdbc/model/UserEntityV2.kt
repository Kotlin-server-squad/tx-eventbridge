package com.tzezula.eventbridge.jdbc.model

import com.tzezula.eventbridge.common.SubscriptionPlan
import com.tzezula.eventbridge.common.User
import com.tzezula.eventbridge.jdbc.event.UserAuditListener
import jakarta.persistence.*
import org.springframework.data.jpa.repository.JpaRepository
import kotlin.jvm.Transient

@Entity(name = "userV2")
@Table(name = "users")
@EntityListeners(UserAuditListener::class)
data class UserEntityV2(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var name: String = "",
    @Column(name = "subscription_plan")
    @Enumerated(EnumType.STRING)
    var plan: SubscriptionPlan = SubscriptionPlan.FREE,
) {
    @Transient
    private var _before: UserEntityV2? = null

    @PostLoad
    @PostPersist
    fun captureLoadedState() {
        // Shallow copy is fine, since this is a data class
        _before = copy()
    }

    // Expose the snapshot of the entity before it was modified
    fun beforeState(): UserEntityV2? = _before
}

interface UserEntityV2Repository : JpaRepository<UserEntityV2, Long>

fun UserEntityV2.toModel(): User {
    return User(
        id = id,
        name = name,
        plan = plan,
    )
}
