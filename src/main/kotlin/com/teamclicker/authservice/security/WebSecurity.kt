package com.teamclicker.authservice.security

import com.teamclicker.authservice.Constants.JWT_HEADER_NAME
import com.teamclicker.authservice.mappers.ClaimsToJWTDataMapper
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class WebSecurity(
    private val claimsToJWTDataMapper: ClaimsToJWTDataMapper,
    private val cryptoKeys: CryptoKeys
) : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http.cors().disable()
            .csrf().disable()
            .httpBasic().disable()
            .formLogin().disable()
            .addFilter(JWTAuthorizationFilter(authenticationManager(), claimsToJWTDataMapper, cryptoKeys))
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val source = UrlBasedCorsConfigurationSource()
        val corsConfig = CorsConfiguration()
            .applyPermitDefaultValues().apply {
                addAllowedHeader(JWT_HEADER_NAME)
                addExposedHeader(JWT_HEADER_NAME)
                addAllowedMethod(HttpMethod.GET)
                addAllowedMethod(HttpMethod.POST)
                addAllowedMethod(HttpMethod.PUT)
                addAllowedMethod(HttpMethod.DELETE)
                addAllowedMethod(HttpMethod.HEAD)
                addAllowedMethod(HttpMethod.OPTIONS)
            }

        source.registerCorsConfiguration("/**", corsConfig)
        return source
    }
}
