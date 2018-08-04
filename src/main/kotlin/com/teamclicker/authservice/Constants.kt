package com.teamclicker.authservice

import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit


object Constants {
    const val JWT_HEADER_NAME = "Authorization"
    const val JWT_TOKEN_PREFIX = "Bearer "

    val JWT_PRIVATE_KEY_NAME = "classpath:jwt_private_key.der"
    val JWT_PUBLIC_KEY_NAME = "classpath:jwt_public_key.der"
    val JWT_EXPIRATION_TIME = 100_000L
    val JWT_EXPIRATION_TIME_UNIT = TimeUnit.DAYS

    const val MIN_PASSWORD_SIZE = 5

    val RESET_PASSWORD_TOKEN_EXPIRATION_TIME_VALUE = 1L
    val RESET_PASSWORD_TOKEN_EXPIRATION_TIME_UNIT = ChronoUnit.DAYS
}