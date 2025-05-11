package com.tzezula.eventbridge.jdbc.config

import com.tzezula.eventbridge.jdbc.event.UserChangedListener
import com.tzezula.eventbridge.jdbc.event.UserCreatedListener
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
