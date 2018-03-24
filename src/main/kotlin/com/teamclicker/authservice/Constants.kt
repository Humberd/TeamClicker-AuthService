package com.teamclicker.authservice


object Constants {
    const val JWT_HEADER_NAME = "Authorization"
    const val JWT_TOKEN_PREFIX = "Bearer "

    val JWT_EXPIRATION_TIME = 864000000 // 10 days

    const val MIN_PASSWORD_SIZE = 5
}