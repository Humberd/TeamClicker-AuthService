package com.teamclicker.authservice.security

import org.springframework.security.core.authority.SimpleGrantedAuthority

data class JWTData(
        val accountId: Long,
        val authenticationMethod: AuthenticationMethod,
        val roles: List<String>
) {
    fun getGrantedAuthorities() =
            roles.map { SimpleGrantedAuthority(it) }
}

