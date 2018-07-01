package com.teamclicker.authservice.testConfig.kafka

import org.springframework.kafka.test.context.EmbeddedKafka

@EmbeddedKafka(
    partitions = 1,
    controlledShutdown = false,
    brokerProperties = ["listeners=PLAINTEXT://localhost:3333", "port=3333"],
)
annotation class EmbeddedKafkaServer {

}