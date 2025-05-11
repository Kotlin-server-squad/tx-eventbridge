package com.tzezula.eventbridge.r2dbc

import com.tzezula.eventbridge.common.SubscriptionPlan
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

@Table(name = "users")
data class UserEntity(
    @Id
    val id: Long? = null,
    var name: String = "",
    @Column("subscription_plan")
    var plan: String = SubscriptionPlan.FREE.name,
)

interface UserEntityRepository : CoroutineCrudRepository<UserEntity, Long>
