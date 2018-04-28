package com.teamclicker.authservice.security

import org.springframework.security.authentication.AbstractAuthenticationToken

class JWTAuthenticationToken(
    val jwtData: JWTData
) : AbstractAuthenticationToken(jwtData.getGrantedAuthorities()) {

    init {
        isAuthenticated = true
    }

    override fun getCredentials(): Any {
        return ""
    }

    override fun getPrincipal(): Any {
        return ""
    }

}