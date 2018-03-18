package com.teamclicker.authservice.testmodels

import com.teamclicker.authservice.dto.EmailPasswordSignInDTO
import com.teamclicker.authservice.dto.EmailPasswordSignUpDTO

data class UserAccountMock(
        var email: String?,
        var password: String?
) {
    fun toEmailPasswordSignUp(): EmailPasswordSignUpDTO {
        return EmailPasswordSignUpDTO().also {
            it.email = email
            it.password = password
        }
    }

    fun toEmailPasswordSignIn(): EmailPasswordSignInDTO {
        return EmailPasswordSignInDTO().also {
            it.email = email
            it.password = password
        }
    }
}