package com.teamclicker.authservice.kafka

enum class KafkaTopic(
    val value: String
) {
    PASSWORD_RESET_EMAIL("passwordResetEmail")
}

val ALL_KAFKA_TOPICS = KafkaTopic.values().map { it.value }.toTypedArray()