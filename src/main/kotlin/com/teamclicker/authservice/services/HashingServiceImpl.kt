package com.teamclicker.authservice.services

import org.springframework.stereotype.Service
import java.security.MessageDigest

@Service
class HashingServiceImpl : HashingService {
    override fun hashBySHA_256(data: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val result = digest.digest(data.toByteArray())
        return bytesToHex(result)
    }

    fun bytesToHex(hash: ByteArray): String {
        val hexString = StringBuffer()
        for (i in hash.indices) {
            val hex = Integer.toHexString(0xff and hash[i].toInt())
            if (hex.length == 1) hexString.append('0')
            hexString.append(hex)
        }
        return hexString.toString()
    }
}