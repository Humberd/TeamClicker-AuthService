package com.teamclicker.authservice.testhelpers

import com.teamclicker.authservice.Constants.JWT_HEADER_NAME
import com.teamclicker.authservice.Constants.JWT_PUBLIC_KEY
import com.teamclicker.authservice.Constants.JWT_TOKEN_PREFIX
import com.teamclicker.authservice.mappers.ClaimsToJWTDataMapper
import com.teamclicker.authservice.security.JWTData
import io.jsonwebtoken.Jwts
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class JwtExtractorHelper(
        private val claimsToJWTDataMapper: ClaimsToJWTDataMapper
) {
    fun getJwtData(response: ResponseEntity<*>): JWTData {
        val token = response.headers.get(JWT_HEADER_NAME)?.get(0)
        val rawToken = token?.replaceFirst(JWT_TOKEN_PREFIX, "")
        val jwtClaims = Jwts.parser()
                .setSigningKey(JWT_PUBLIC_KEY)
                .parseClaimsJws(rawToken)
                .getBody()

        return claimsToJWTDataMapper.parse(jwtClaims)
    }
}