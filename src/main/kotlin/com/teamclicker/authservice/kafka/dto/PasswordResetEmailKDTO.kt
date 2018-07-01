package com.teamclicker.authservice.kafka.dto

data class PasswordResetEmailKDTO(
    val email: String = "",
    val token: String = ""
)