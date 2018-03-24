package com.teamclicker.authservice


object Constants {
    const val JWT_HEADER_NAME = "Authorization"
    const val JWT_TOKEN_PREFIX = "Bearer "

    val JWT_PRIVATE_KEY_NAME = "jwt_private_key.der"
    val JWT_PUBLIC_KEY_NAME = "jwt_public_key.der"
    val JWT_EXPIRATION_TIME = 864000000 // 10 days

    const val MIN_PASSWORD_SIZE = 5
}