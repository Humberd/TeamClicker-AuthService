package com.teamclicker.authservice.mappers

import com.teamclicker.authservice.security.AuthenticationMethod
import com.teamclicker.authservice.security.JWTData
import com.teamclicker.authservice.security.UserRole
import io.jsonwebtoken.Claims
import org.springframework.stereotype.Service

@Service
class ClaimsToJWTDataMapper : AbstractMapper<Claims, JWTData>() {
    override fun parse(from: Claims): JWTData {
        return JWTData(
                accountId = from.get("accountId", java.lang.Long::class.java).toLong(),
                authenticationMethod = from.get("authenticationMethod", String::class.java)
                        .let { AuthenticationMethod.valueOf(it) },
                roles = from.get("roles", userRolesListType)
                        .map { UserRole.valueOf(it) }
        )
    }

    companion object {
        val userRolesListType = arrayListOf<String>()::class.java
    }
}