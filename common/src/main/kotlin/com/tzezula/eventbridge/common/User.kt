package com.tzezula.eventbridge.common

data class User(
    val id: Long?,
    val name: String,
    val plan: SubscriptionPlan,
)
