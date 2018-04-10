package com.teamclicker.authservice.testmodels

import com.teamclicker.authservice.dao.EmailPasswordAuthDAO
import com.teamclicker.authservice.dto.EPSignInDTO
import com.teamclicker.authservice.dto.EPSignUpDTO

data class UserAccountMock(
    var email: String?,
    var password: String?
) {
    fun toEmailPasswordSignUp(): EPSignUpDTO {
        return EPSignUpDTO().also {
            it.email = email
            it.password = password
        }
    }

    fun toEmailPasswordSignIn(): EPSignInDTO {
        return EPSignInDTO().also {
            it.email = email
            it.password = password
        }
    }

    fun toEmailPasswordAuthDAO(): EmailPasswordAuthDAO {
        return EmailPasswordAuthDAO().also {
            it.email = email
            it.password = password
        }
    }
}