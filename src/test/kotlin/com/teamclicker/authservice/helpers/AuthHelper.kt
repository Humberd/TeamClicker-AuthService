package com.teamclicker.authservice.helpers

import com.teamclicker.authservice.models.SpringErrorResponse
import com.teamclicker.authservice.models.UserAccountMock
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.ResponseEntity
import kotlin.reflect.KClass

class AuthHelper(private val http: TestRestTemplate) {
    companion object {
        val ALICE
            get() = UserAccountMock(
                    email = "alice@alice.com",
                    password = "alicePassword"
            )
        val BOB
            get() = UserAccountMock(
                    email = "bobe@bob.com",
                    password = "bobPassword"
            )
        val CHUCK
            get() = UserAccountMock(
                    email = "chuck@chuck.com",
                    password = "chuckPassword"
            )
    }

    fun signUp() = EmailPasswordSignUpBuilder()
    inner class EmailPasswordSignUpBuilder {
        private lateinit var user: UserAccountMock

        fun with(user: UserAccountMock): EmailPasswordSignUpBuilder {
            this.user = user
            return this
        }

        fun <Err : Any> expectError(type: KClass<Err>): ResponseEntity<Err> {
            return build(type.java)
        }

        fun expectError(): ResponseEntity<SpringErrorResponse> {
            return build(SpringErrorResponse::class.java)
        }

        fun expectSuccess(): ResponseEntity<String> {
            return build(String::class.java)
        }

        private fun <T> build(type: Class<T>): ResponseEntity<T> {
            return http.postForEntity(
                    "/api/auth/emailPassword/signUp",
                    user.toEmailPasswordSignUp(),
                    type)
        }
    }

    fun signIn() = EmailPasswordSignInBuilder()
    inner class EmailPasswordSignInBuilder {
        private lateinit var user: UserAccountMock

        fun with(user: UserAccountMock): EmailPasswordSignInBuilder {
            this.user = user
            return this
        }

        fun <Err : Any> expectError(type: KClass<Err>): ResponseEntity<Err> {
            return build(type.java)
        }

        fun expectError(): ResponseEntity<SpringErrorResponse> {
            return build(SpringErrorResponse::class.java)
        }

        fun expectSuccess(): ResponseEntity<String> {
            return build(String::class.java)
        }

        private fun <T> build(type: Class<T>): ResponseEntity<T> {
            this@AuthHelper.signUp()
                    .with(user)
                    .expectSuccess()

            return http.postForEntity(
                    "/api/auth/emailPassword/signIn",
                    user.toEmailPasswordSignIn(),
                    type)
        }
    }

}