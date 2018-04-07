package com.teamclicker.authservice.testhelpers

import com.teamclicker.authservice.Constants.JWT_HEADER_NAME
import com.teamclicker.authservice.Constants.JWT_TOKEN_PREFIX
import com.teamclicker.authservice.mappers.ClaimsToJWTDataMapper
import com.teamclicker.authservice.security.CryptoKeys
import com.teamclicker.authservice.security.JWTData
import io.jsonwebtoken.Jwts
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class JwtExtractorHelper(
    private val claimsToJWTDataMapper: ClaimsToJWTDataMapper,
    private val cryptoKeys: CryptoKeys
) {
    fun getJwtData(response: ResponseEntity<*>): JWTDataTestWrapper {
        val token = response.headers.get(JWT_HEADER_NAME)?.get(0)
        val rawToken = token?.replaceFirst(JWT_TOKEN_PREFIX, "")
        val jwtClaims = Jwts.parser()
            .setSigningKey(cryptoKeys.JWT_PUBLIC_KEY)
            .parseClaimsJws(rawToken)
            .getBody()

        val jwtData= claimsToJWTDataMapper.parse(jwtClaims)
        return JWTDataTestWrapper(
            jwtData.accountId,
            jwtData.authenticationMethod,
            jwtData.roles,
            token!!
        )
    }
}