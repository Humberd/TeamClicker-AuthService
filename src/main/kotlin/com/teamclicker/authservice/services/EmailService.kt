package com.teamclicker.authservice.services 

interface EmailService {
    fun sendPasswordResetEmail(email: String, token: String)
}