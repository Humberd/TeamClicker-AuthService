package com.teamclicker.authservice.mappers

import com.teamclicker.authservice.security.JWTData
import io.jsonwebtoken.Claims
import io.jsonwebtoken.impl.DefaultClaims
import org.springframework.stereotype.Service

@Service
class JWTDataToClaimsMapper : AbstractMapper<JWTData, Claims>() {
    override fun parse(from: JWTData): Claims {
        return DefaultClaims(
            mapOf(
                "accountId" to from.accountId,
                "authenticationMethod" to from.authenticationMethod.name,
                "roles" to from.roles
            )
        )
    }
}