package com.teamclicker.authservice.security

import com.teamclicker.authservice.Constants.JWT_EXPIRATION_TIME
import com.teamclicker.authservice.Constants.JWT_HEADER_NAME
import com.teamclicker.authservice.Constants.JWT_TOKEN_PREFIX
import com.teamclicker.authservice.dao.AuthenticationMethod
import com.teamclicker.authservice.dao.UserAccountDAO
import com.teamclicker.authservice.mappers.JWTDataToClaimsMapper
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import java.util.*

@Service
class JWTHelper(
    private val jwtDataToClaimsMapper: JWTDataToClaimsMapper,
    private val cryptoKeys: CryptoKeys
) {
    fun convertUserAccountToJwtString(userAccount: UserAccountDAO, authenticationMethod: AuthenticationMethod): String {
        val customClaims = jwtDataToClaimsMapper.parse(
            JWTData(
                accountId = userAccount.id!!,
                roles = userAccount.roles.map { it.id!! }.toSet(),
                authenticationMethod = authenticationMethod
            )
        )

        return Jwts.builder()
            .setClaims(customClaims)
            .setId(UUID.randomUUID().toString())
            .setExpiration(Date(System.currentTimeMillis() + JWT_EXPIRATION_TIME))
            .setIssuedAt(Date())
            .signWith(SignatureAlgorithm.RS512, cryptoKeys.JWT_PRIVATE_KEY)
            .compact()
    }

    fun getHeaders(jwtString: String): MultiValueMap<String, String> {
        return LinkedMultiValueMap<String, String>(1).also {
            it.add(JWT_HEADER_NAME, JWT_TOKEN_PREFIX + jwtString)
        }
    }
}