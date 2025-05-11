package com.tzezula.eventbridge.r2dbc.config

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

@TestConfiguration
class TestPostgresConfig {
    companion object {
        private val Postgres = PostgreSQLContainer(DockerImageName.parse("postgres:16"))
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test")
        init {
            Postgres.start()
        }
    }

    @DynamicPropertySource
    fun overrideDatasourceProps(registry: DynamicPropertyRegistry) {
        registry.add("spring.r2dbc.url") { Postgres.jdbcUrl }
        registry.add("spring.r2dbc.username") { Postgres.username }
        registry.add("spring.r2dbc.password") { Postgres.password }
    }
}
