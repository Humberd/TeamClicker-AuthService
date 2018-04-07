package com.teamclicker.authservice.controllers.helpers

import com.teamclicker.authservice.dto.EmailPasswordChangePasswordDTO
import com.teamclicker.authservice.dto.EmailPasswordSignInDTO
import com.teamclicker.authservice.dto.EmailPasswordSignUpDTO
import com.teamclicker.authservice.testhelpers.JwtExtractorHelper
import com.teamclicker.authservice.testmodels.UserAccountMock
import org.junit.jupiter.api.Assertions
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class EmailPasswordAuthControllerHelper(
    private val http: TestRestTemplate,
    private val jwtExtractorHelper: JwtExtractorHelper
) {
    fun signUp() = SignUpEndpointBuilder()

    /**
     * Signs up a [user] and returns its Jwt data
     */
    fun signUp(user: UserAccountMock) = signUp()
        .with(user)
        .expectSuccess().also {
            Assertions.assertEquals(HttpStatus.OK, it.statusCode)
        }.let {
            jwtExtractorHelper.getJwtData(it)
        }

    fun signIn() = SignInEndpointBuilder()

    fun changePassword() = ChangePasswordEndpointBuilder()

    inner class SignUpEndpointBuilder :
        EndpointBuilder<SignUpEndpointBuilder, EmailPasswordSignUpDTO, Void>(Void::class.java, http) {
        override fun with(user: UserAccountMock?): SignUpEndpointBuilder {
            sending(user?.toEmailPasswordSignUp())
            return this
        }

        override fun <T> build(
            httpEntity: HttpEntity<EmailPasswordSignUpDTO>,
            responseBodyType: Class<T>
        ): ResponseEntity<T> {
            return http.postForEntity(
                "/api/auth/emailPassword/signUp",
                httpEntity,
                responseBodyType
            )
        }
    }

    inner class SignInEndpointBuilder :
        EndpointBuilder<SignInEndpointBuilder, EmailPasswordSignInDTO, Void>(Void::class.java, http) {
        override fun with(user: UserAccountMock?): SignInEndpointBuilder {
            sending(user?.toEmailPasswordSignIn())
            return this
        }

        override fun <T> build(
            httpEntity: HttpEntity<EmailPasswordSignInDTO>,
            responseBodyType: Class<T>
        ): ResponseEntity<T> {
            return http.postForEntity(
                "/api/auth/emailPassword/signIn",
                httpEntity,
                responseBodyType
            )
        }
    }

    inner class ChangePasswordEndpointBuilder :
        EndpointBuilder<ChangePasswordEndpointBuilder, EmailPasswordChangePasswordDTO, Void>(
            Void::class.java,
            http
        ) {

        override fun <T> build(
            httpEntity: HttpEntity<EmailPasswordChangePasswordDTO>,
            responseBodyType: Class<T>
        ): ResponseEntity<T> {
            return http.postForEntity(
                "/api/auth/emailPassword/changePassword",
                httpEntity,
                responseBodyType
            )
        }
    }
}