package com.tzezula.eventbridge.r2dbc

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.transaction.annotation.EnableTransactionManagement

@SpringBootApplication
@EnableTransactionManagement
@EnableAsync
class R2dbcApplication

fun main(args: Array<String>) {
    runApplication<R2dbcApplication>(*args)
}
