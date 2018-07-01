package com.teamclicker.authservice.testConfig.models

import com.teamclicker.authservice.services.EmailService

open class EmailServiceMock : EmailService {
    var email: String? = null
    var token: String? = null

    override fun sendPasswordResetEmail(email: String, token: String) {
        this.email = email
        this.token = token
    }
}