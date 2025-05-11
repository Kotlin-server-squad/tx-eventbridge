package com.tzezula.eventbridge.jdbc

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TestConfig {

    @Bean
    fun userCreatedListener(): UserCreatedListener {
        return UserCreatedListener()
    }
}
