package com.tzezula.eventbridge.jdbc

import com.tzezula.eventbridge.common.SubscriptionPlan
import jakarta.persistence.*
import org.springframework.data.jpa.repository.JpaRepository

@Entity(name = "users")
class UserEntity(
    var name: String = "",
    @Column(name = "subscription_plan")
    @Enumerated(EnumType.STRING)
    val plan: SubscriptionPlan = SubscriptionPlan.FREE,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}

interface UserEntityRepository : JpaRepository<UserEntity, Long>
