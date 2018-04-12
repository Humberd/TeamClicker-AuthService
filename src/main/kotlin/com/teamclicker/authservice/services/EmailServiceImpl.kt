package com.teamclicker.authservice.services

import mu.KLogging
import org.springframework.stereotype.Service

@Service
class EmailServiceImpl : EmailService {
    override fun sendPasswordResetEmail(email: String, token: String) {
        logger.error { "Sending Password Reset Email is not implemented yet" }
    }

    companion object : KLogging()
}