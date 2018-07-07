package com.teamclicker.authservice.testConfig.kafka

import com.teamclicker.authservice.kafka.ALL_KAFKA_TOPICS
import mu.KLogging
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.TopicPartition
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.listener.ConsumerSeekAware
import org.springframework.kafka.listener.KafkaMessageListenerContainer
import org.springframework.kafka.listener.MessageListener
import org.springframework.kafka.listener.config.ContainerProperties
import org.springframework.stereotype.Service
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import javax.annotation.PostConstruct
import kotlin.reflect.KClass


@Service
class KafkaMockConsumer(
    val consumerFactory: ConsumerFactory<String, Any>
) {
    lateinit var container: KafkaMessageListenerContainer<String, Any>
    lateinit var records: LinkedBlockingQueue<ConsumerRecord<String, Any>>

    @PostConstruct
    fun setUp() {
        // set the topic that needs to be consumed
        val containerProperties = ContainerProperties(*ALL_KAFKA_TOPICS)

        // create a thread safe queue to store the received message
        records = LinkedBlockingQueue()

        // create a Kafka MessageListenerContainer
        container = KafkaMessageListenerContainer(consumerFactory, containerProperties).also {
            // setup a Kafka MessageListener
            /* Have to also implement ConsumerSeekAware, which rewinds current offest to the latest
            * Otherwise This listener would receive like 20+ previous message */
            it.setupMessageListener(object : MessageListener<String, Any>, ConsumerSeekAware {
                override fun onIdleContainer(
                    assignments: MutableMap<TopicPartition, Long>?,
                    callback: ConsumerSeekAware.ConsumerSeekCallback?
                ) {
                }

                override fun onPartitionsAssigned(
                    assignments: MutableMap<TopicPartition, Long>,
                    callback: ConsumerSeekAware.ConsumerSeekCallback
                ) {
                    assignments.forEach { t, o -> callback.seekToEnd(t.topic(), t.partition()) }
                }

                override fun registerSeekCallback(callback: ConsumerSeekAware.ConsumerSeekCallback?) {
                }

                override fun onMessage(record: ConsumerRecord<String, Any>) {
                    logger.info { "Received Kafka Message: ${record.value().javaClass.simpleName}" }
                    records.add(record)
                }
            })
            // start the container and underlying message listener
            it.start()
        }
    }

//    fun tearDown() {
//        // stop the container
//        container.stop()
//    }

    fun clearRecords() {
        records.clear()
    }

    @Suppress("UNCHECKED_CAST")
    fun <T: Any> getLatestMessageAs(type: KClass<T>): T {
        val received = records.poll(10, TimeUnit.SECONDS)
        return received.value() as T
    }

    fun waitForMessage() {
        records.poll(10, TimeUnit.SECONDS)
    }

    companion object : KLogging()
}