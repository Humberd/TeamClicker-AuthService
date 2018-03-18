package com.teamclicker.authservice.security

import com.teamclicker.authservice.Constants
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
        private val jwtDataToClaimsMapper: JWTDataToClaimsMapper
) {
    fun convertUserAccountToJwtString(userAccount: UserAccountDAO, authenticationMethod: AuthenticationMethod): String {
        val customClaims = jwtDataToClaimsMapper.parse(JWTData(
                accountId = userAccount.id!!,
                roles = listOf(UserRole.ADMIN),
                authenticationMethod = authenticationMethod
        ))

        return Jwts.builder()
                .setClaims(customClaims)
                .setId(UUID.randomUUID().toString())
                .setExpiration(Date(System.currentTimeMillis() + Constants.JWT_EXPIRATION_TIME))
                .setIssuedAt(Date())
                .signWith(SignatureAlgorithm.HS512, Constants.JWT_SECRET)
                .compact()
    }

    fun getHeaders(jwtString: String): MultiValueMap<String, String> {
        return LinkedMultiValueMap<String, String>(1).also {
            it.add(Constants.JWT_HEADER_NAME, Constants.JWT_TOKEN_PREFIX + jwtString)
        }
    }
}