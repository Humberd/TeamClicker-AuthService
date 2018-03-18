package com.teamclicker.authservice

import com.teamclicker.authservice.security.CryptoKeysExtractor


object Constants {
    const val JWT_HEADER_NAME = "Authorization"
    const val JWT_TOKEN_PREFIX = "Bearer "

    val JWT_PRIVATE_KEY = CryptoKeysExtractor().getPrivateRSAKey("src/main/resources/private_key.der")
    val JWT_PUBLIC_KEY = CryptoKeysExtractor().getPublicRSAKey("src/main/resources/public_key.der")
    val JWT_EXPIRATION_TIME = 864000000 // 10 days

    const val MIN_PASSWORD_SIZE = 5
}