package com.teamclicker.authservice.testConfig.kafka

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.test.rule.KafkaEmbedded
import org.springframework.stereotype.Service

@Service
class Foo {

    @Bean
    fun aaa(): KafkaEmbedded{
        return KafkaEmbedded(1, true, 1)
            .brokerProperties(mapOf(
                "listeners" to "PLAINTEXT://localhost:3333",
                "port" to "333"
            ))
    }
}