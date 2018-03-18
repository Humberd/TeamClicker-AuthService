package com.teamclicker.authservice.security

import org.springframework.security.authentication.AbstractAuthenticationToken

class JWTAuthenticationToken(
        val jwtData: JWTData
) : AbstractAuthenticationToken(jwtData.getGrantedAuthorities()) {

    init {
        isAuthenticated = true
    }

    override fun getCredentials(): Any {
        return "foobar"
    }

    override fun getPrincipal(): Any {
        return "myprincipal"
    }

}