package com.teamclicker.authservice.services

interface HashingService {
    fun hashBySHA_256(data: String): String
}