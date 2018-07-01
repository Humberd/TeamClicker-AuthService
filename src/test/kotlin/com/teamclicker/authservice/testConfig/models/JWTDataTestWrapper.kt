package com.teamclicker.authservice.testConfig.models

import com.teamclicker.authservice.security.AuthenticationMethod

data class JWTDataTestWrapper(
    val accountId: Long,
    val authenticationMethod: AuthenticationMethod,
    val roles: Set<String>,
    val token: String
)

