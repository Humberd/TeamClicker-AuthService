package com.teamclicker.authservice.testConfig.kafka

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.test.rule.KafkaEmbedded
import org.springframework.kafka.test.utils.KafkaTestUtils

@TestConfiguration
class KafkaTestConfig(
    val kafkaEmbedded: KafkaEmbedded
) {

    @Bean
    fun producerFactory(): ProducerFactory<String, String> {
        return DefaultKafkaProducerFactory(KafkaTestUtils.producerProps(kafkaEmbedded))
    }

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, String> {
        val kafkaTemplate = KafkaTemplate(producerFactory())
        kafkaTemplate.defaultTopic = "defaultTopic"
        return kafkaTemplate
    }
}