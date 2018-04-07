package com.teamclicker.authservice.controllers.helpers

import com.teamclicker.authservice.dto.EmailPasswordChangePasswordDTO
import com.teamclicker.authservice.dto.EmailPasswordSignInDTO
import com.teamclicker.authservice.dto.EmailPasswordSignUpDTO
import com.teamclicker.authservice.testmodels.UserAccountMock
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.ResponseEntity

class EmailPasswordAuthControllerHelper(private val http: TestRestTemplate) {
    inner class SignUpEndpointBuilder :
        EndpointBuilder<SignUpEndpointBuilder, EmailPasswordSignUpDTO, Void>(Void::class.java, http) {
        override fun with(user: UserAccountMock?): SignUpEndpointBuilder {
            sending(user?.toEmailPasswordSignUp())
            return this
        }

        override fun <T> build(type: Class<T>): ResponseEntity<T> {
            return http.postForEntity(
                "/api/auth/emailPassword/signUp",
                httpEntity,
                type
            )
        }
    }

    inner class SignInEndpointBuilder :
        EndpointBuilder<SignInEndpointBuilder, EmailPasswordSignInDTO, Void>(Void::class.java, http) {
        override fun with(user: UserAccountMock?): SignInEndpointBuilder {
            sending(user?.toEmailPasswordSignIn())
            return this
        }

        override fun <T> build(type: Class<T>): ResponseEntity<T> {
            return http.postForEntity(
                "/api/auth/emailPassword/signIn",
                httpEntity,
                type
            )
        }
    }

    inner class ChangePasswordEndpointBuilder :
        EndpointBuilder<ChangePasswordEndpointBuilder, EmailPasswordChangePasswordDTO, Void>(
            Void::class.java,
            http
        ) {
        override fun <T> build(type: Class<T>): ResponseEntity<T> {
            return http.postForEntity(
                "/api/auth/emailPassword/changePassword",
                httpEntity,
                type
            )
        }
    }
}