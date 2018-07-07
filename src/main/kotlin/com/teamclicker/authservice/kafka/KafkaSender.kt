package com.teamclicker.authservice.kafka

import com.teamclicker.authservice.kafka.dto.PasswordResetEmailKDTO
import mu.KLogging
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class KafkaSender(
    private val kafkaTemplate: KafkaTemplate<String, Any>
) {
    internal fun send(topic: KafkaTopic, data: Any) {
        logger.info { "Send Kafka Message: ${data.javaClass.simpleName}" }
        kafkaTemplate.send(topic.value, data)
    }

    fun send(passwordResetEmailKDTO: PasswordResetEmailKDTO) {
        send(KafkaTopic.PASSWORD_RESET_EMAIL, passwordResetEmailKDTO)
    }

    companion object: KLogging()
}