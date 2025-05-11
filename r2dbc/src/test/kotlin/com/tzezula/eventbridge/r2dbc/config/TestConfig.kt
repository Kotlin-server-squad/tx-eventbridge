package com.tzezula.eventbridge.r2dbc.config

import com.tzezula.eventbridge.r2dbc.event.UserChangedListener
import com.tzezula.eventbridge.r2dbc.event.UserCreatedListener
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TestConfig {

    @Bean
    fun userCreatedListener(): UserCreatedListener {
        return UserCreatedListener()
    }

    @Bean
    fun userChangedListener(): UserChangedListener {
        return UserChangedListener()
    }
}
