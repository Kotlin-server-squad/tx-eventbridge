package com.tzezula.eventbridge.common

data class UserChangedEvent(
    val oldUser: User,
    val newUser: User,
) : Event
