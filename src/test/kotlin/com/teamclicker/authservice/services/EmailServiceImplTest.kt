package com.teamclicker.authservice.services

import com.teamclicker.authservice.kafka.*
import com.teamclicker.authservice.kafka.dto.PasswordResetEmailKDTO
import com.teamclicker.authservice.testConfig.extensions.fromJson
import com.teamclicker.authservice.testConfig.extensions.toJson
import mu.KLogging
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.KafkaMessageListenerContainer
import org.springframework.kafka.listener.MessageListener
import org.springframework.kafka.listener.config.ContainerProperties
import org.springframework.kafka.test.rule.KafkaEmbedded
import org.springframework.kafka.test.utils.ContainerTestUtils
import org.springframework.kafka.test.utils.KafkaTestUtils
import org.springframework.test.context.junit.jupiter.SpringExtension
import com.teamclicker.authservice.testConfig.kafka.EmbeddedKafkaServer
import com.teamclicker.authservice.testConfig.kafka.KafkaMockConsumer
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit


@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@EmbeddedKafkaServer
class EmailServiceImplTest {

    companion object : KLogging()

    @Autowired
    lateinit var emailServiceImpl: EmailServiceImpl

    @Autowired
    lateinit var kafkaEmbedded: KafkaEmbedded
    lateinit var kafkaMockConsumer: KafkaMockConsumer

    @BeforeAll
    fun setUpAll() {
        kafkaMockConsumer = KafkaMockConsumer(kafkaEmbedded)
    }

    @BeforeEach
    fun setUpEach() {
        kafkaMockConsumer.clearRecords()
    }

    @AfterAll
    fun tearDown() {
        kafkaMockConsumer.tearDown()
    }

    @Nested
    inner class SendPasswordResetEmail {
        @Test
        fun `should send Kafka message`() {
            emailServiceImpl.sendPasswordResetEmail("admin@admin.com", "qwerty")

            val received = kafkaMockConsumer.records.poll(10, TimeUnit.SECONDS)

            assertEquals(KafkaTopic.PASSWORD_RESET_EMAIL.value, received.topic())
            assertEquals(
                PasswordResetEmailKDTO("admin@admin.com", "qwerty"),
                received.value().fromJson(PasswordResetEmailKDTO::class.java)
            )
        }
    }
}