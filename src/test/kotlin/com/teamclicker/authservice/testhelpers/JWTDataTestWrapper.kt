package com.teamclicker.authservice.testhelpers

import com.teamclicker.authservice.dao.AuthenticationMethod

data class JWTDataTestWrapper(
    val accountId: Long,
    val authenticationMethod: AuthenticationMethod,
    val roles: List<String>,
    val token: String
) {
}
