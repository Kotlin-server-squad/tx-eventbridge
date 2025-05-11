package com.tzezula.eventbridge.common

data class UserChangedEvent(
    val userId: Long,
    val oldPlan: SubscriptionPlan,
    val newPlan: SubscriptionPlan,
)
