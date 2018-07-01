package com.teamclicker.authservice.testConfig.kafka

import com.teamclicker.authservice.kafka.ALL_KAFKA_TOPICS
import com.teamclicker.authservice.kafka.dto.PasswordResetEmailKDTO
import com.teamclicker.authservice.testConfig.extensions.fromJson
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.KafkaMessageListenerContainer
import org.springframework.kafka.listener.MessageListener
import org.springframework.kafka.listener.config.ContainerProperties
import org.springframework.kafka.test.rule.KafkaEmbedded
import org.springframework.kafka.test.utils.ContainerTestUtils
import org.springframework.kafka.test.utils.KafkaTestUtils
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit

class KafkaMockConsumer(
    private val kafkaEmbedded: KafkaEmbedded
) {
    lateinit var container: KafkaMessageListenerContainer<String, String>
    lateinit var records: LinkedBlockingQueue<ConsumerRecord<String, String>>

    init {
        setUp()
    }

    fun setUp() {
        // set up the Kafka consumer properties
        val consumerProperties = KafkaTestUtils.consumerProps("AuthService", "false", kafkaEmbedded)

        // create a Kafka consumer factory
        val consumerFactory = DefaultKafkaConsumerFactory<String, String>(consumerProperties)

        // set the topic that needs to be consumed
        val containerProperties = ContainerProperties(*ALL_KAFKA_TOPICS)

        // create a thread safe queue to store the received message
        records = LinkedBlockingQueue()

        // create a Kafka MessageListenerContainer
        container = KafkaMessageListenerContainer(consumerFactory, containerProperties).also {
            // setup a Kafka message listener
            it.setupMessageListener(object : MessageListener<String, String> {
                override fun onMessage(record: ConsumerRecord<String, String>) {
                    records.add(record)
                }
            })
            // start the container and underlying message listener
            it.start()
        }

        // wait until the container has the required number of assigned partitions
        ContainerTestUtils.waitForAssignment(container, kafkaEmbedded.getPartitionsPerTopic())
    }

    fun tearDown() {
        // stop the container
        container.stop()
    }

    fun clearRecords() {
        records.clear()
    }

    fun <T> getLatestMessageAs(type: Class<T>): T {
        val received = records.poll(10, TimeUnit.SECONDS)
        return received.value().fromJson(type)
    }

    fun waitForMessage() {
        records.poll(10, TimeUnit.SECONDS)
    }
}