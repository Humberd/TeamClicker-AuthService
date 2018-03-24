package com.teamclicker.authservice.security

import java.io.File
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec

class CryptoKeysExtractor {
    fun getPrivateRSAKey(path: String): PrivateKey {
        val keyBytes = try {
            File(path).readBytes()
        } catch (e: Exception) {
            System.getenv("TC_JWT_PRIVATE_KEY").toByteArray()
        }
        val keySpec = PKCS8EncodedKeySpec(keyBytes)
        return KeyFactory.getInstance("RSA").generatePrivate(keySpec)
    }

    fun getPublicRSAKey(path: String): PublicKey {
        val keyBytes = try {
            File(path).readBytes()
        } catch (e: Exception) {
            System.getenv("TC_JWT_PUBLIC_KEY").toByteArray()
        }
        val keySpec = X509EncodedKeySpec(keyBytes)
        return KeyFactory.getInstance("RSA").generatePublic(keySpec)
    }
}