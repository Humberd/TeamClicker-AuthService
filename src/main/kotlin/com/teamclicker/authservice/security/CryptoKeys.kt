package com.teamclicker.authservice.security

import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec

@Service
class CryptoKeys {
    final val JWT_PRIVATE_KEY: PrivateKey
    final val JWT_PUBLIC_KEY: PublicKey

    init {
        JWT_PRIVATE_KEY = this.getPrivateRSAKey("private_key.der")
        JWT_PUBLIC_KEY = this.getPublicRSAKey("public_key.der")
    }

    fun getPrivateRSAKey(path: String): PrivateKey {
        val keyBytes = ClassPathResource(path).file.readBytes()
        val keySpec = PKCS8EncodedKeySpec(keyBytes)
        return KeyFactory.getInstance("RSA").generatePrivate(keySpec)
    }

    fun getPublicRSAKey(path: String): PublicKey {
        val keyBytes = ClassPathResource(path).file.readBytes()
        val keySpec = X509EncodedKeySpec(keyBytes)
        return KeyFactory.getInstance("RSA").generatePublic(keySpec)
    }
}