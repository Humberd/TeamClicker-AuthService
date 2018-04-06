package com.teamclicker.authservice.security

import com.teamclicker.authservice.Constants.JWT_PRIVATE_KEY_NAME
import com.teamclicker.authservice.Constants.JWT_PUBLIC_KEY_NAME
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Service
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec

@Service
class CryptoKeys(private val resourceLoader: ResourceLoader) {
    final val JWT_PRIVATE_KEY: PrivateKey
    final val JWT_PUBLIC_KEY: PublicKey

    init {
        JWT_PRIVATE_KEY = this.getPrivateRSAKey(JWT_PRIVATE_KEY_NAME)
        JWT_PUBLIC_KEY = this.getPublicRSAKey(JWT_PUBLIC_KEY_NAME)
    }

    fun getPrivateRSAKey(path: String): PrivateKey {
        val keyBytes = resourceLoader.getResource(path).inputStream.readBytes()
        val keySpec = PKCS8EncodedKeySpec(keyBytes)
        return KeyFactory.getInstance("RSA").generatePrivate(keySpec)
    }

    fun getPublicRSAKey(path: String): PublicKey {
        val keyBytes = resourceLoader.getResource(path).inputStream.readBytes()
        val keySpec = X509EncodedKeySpec(keyBytes)
        return KeyFactory.getInstance("RSA").generatePublic(keySpec)
    }
}