package com.teamclicker.authservice.security

import java.io.File
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec

class CryptoKeysExtractor {
    fun getPrivateRSAKey(path: String): PrivateKey {
        val fileContent = File(path).readBytes()
        val keySpec = PKCS8EncodedKeySpec(fileContent)
        return KeyFactory.getInstance("RSA").generatePrivate(keySpec)
    }

    fun getPublicRSAKey(path: String): PublicKey {
        val fileContent = File(path).readBytes()
        val keySpec = X509EncodedKeySpec(fileContent)
        return KeyFactory.getInstance("RSA").generatePublic(keySpec)
    }
}