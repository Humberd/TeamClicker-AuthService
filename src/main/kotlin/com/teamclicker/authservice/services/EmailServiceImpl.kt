package com.teamclicker.authservice.services

import com.teamclicker.authservice.kafka.KafkaSender
import com.teamclicker.authservice.kafka.KafkaTopic
import com.teamclicker.authservice.kafka.dto.PasswordResetEmailKDTO
import org.springframework.stereotype.Service

@Service
class EmailServiceImpl(
    private val kafkaSender: KafkaSender
) : EmailService {

    override fun sendPasswordResetEmail(email: String, token: String) {
        kafkaSender.send(PasswordResetEmailKDTO(email, token))
    }
}