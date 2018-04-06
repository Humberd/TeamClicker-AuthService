package com.teamclicker.authservice.testhelpers

import com.teamclicker.authservice.Constants.JWT_HEADER_NAME
import com.teamclicker.authservice.dto.EmailPasswordChangePasswordDTO
import com.teamclicker.authservice.testmodels.SpringErrorResponse
import com.teamclicker.authservice.testmodels.UserAccountMock
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
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

    @Suppress("UNCHECKED_CAST")
    abstract inner class Builder<Child, Body> {
        protected var user: UserAccountMock? = null
        protected var body: Body? = null

        fun with(user: UserAccountMock): Child {
            this.user = user
            return this as Child
        }

        fun sending(body: Body): Child {
            this.body = body
            return this as Child
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

        abstract protected fun <T> build(type: Class<T>): ResponseEntity<T>
    }

    fun signUp() = EmailPasswordSignUpBuilder()
    inner class EmailPasswordSignUpBuilder : Builder<EmailPasswordSignUpBuilder, Void>() {
        override fun <T> build(type: Class<T>): ResponseEntity<T> {
            return http.postForEntity(
                "/api/auth/emailPassword/signUp",
                user?.toEmailPasswordSignUp(),
                type
            )
        }
    }

    fun signIn() = EmailPasswordSignInBuilder()
    inner class EmailPasswordSignInBuilder : Builder<EmailPasswordSignInBuilder, Void>() {
        override fun <T> build(type: Class<T>): ResponseEntity<T> {
            this@AuthHelper.signUp()
                .with(user!!)
                .expectSuccess()

            return http.postForEntity(
                "/api/auth/emailPassword/signIn",
                user?.toEmailPasswordSignIn(),
                type
            )
        }
    }

    fun changePassword() = EmailPasswordChangePasswordBuilder()
    inner class EmailPasswordChangePasswordBuilder :
        Builder<EmailPasswordChangePasswordBuilder, EmailPasswordChangePasswordDTO>() {
        override fun <T> build(type: Class<T>): ResponseEntity<T> {
            val jwt = this@AuthHelper.signUp()
                .with(user!!)
                .expectSuccess().headers[JWT_HEADER_NAME]

            val headers = HttpHeaders()
            headers.set(JWT_HEADER_NAME, jwt)
            val httpEntity = HttpEntity(body, headers)

            return http.postForEntity(
                "/api/auth/emailPassword/changePassword",
                httpEntity,
                type
            )
        }
    }

}