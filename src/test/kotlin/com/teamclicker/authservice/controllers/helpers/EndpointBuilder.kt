package com.teamclicker.authservice.controllers.helpers

import com.teamclicker.authservice.Constants.JWT_HEADER_NAME
import com.teamclicker.authservice.testmodels.SpringErrorResponse
import com.teamclicker.authservice.testmodels.UserAccountMock
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.util.MultiValueMap
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
abstract class EndpointBuilder<Child, Body, Response>(
    val responseType: Class<Response>,
    val http: TestRestTemplate
) {
    protected var body: Body? = null
    protected val headers: MultiValueMap<String, String> = HttpHeaders()
    protected lateinit var httpEntity: HttpEntity<Body>

    open fun with(user: UserAccountMock?): Child {
        if (user !== HttpConstants.ANONYMOUS) {
            val jwt = http.postForEntity(
                "/api/auth/emailPassword/signIn",
                user?.toEmailPasswordSignIn(),
                Any::class.java
            ).also {
                assertEquals(HttpStatus.OK, it.statusCode)
            }.headers[JWT_HEADER_NAME]

            headers.set(JWT_HEADER_NAME, jwt)
        }

        return this as Child
    }

    open fun sending(body: Body?): Child {
        this.body = body
        return this as Child
    }

    fun <Err : Any> expectError(type: KClass<Err>): ResponseEntity<Err> {
        composeHttpEntity()
        return build(type.java)
    }

    fun expectError(): ResponseEntity<SpringErrorResponse> {
        composeHttpEntity()
        return build(SpringErrorResponse::class.java)
    }

    fun expectSuccess(): ResponseEntity<Response> {
        composeHttpEntity()
        return build(responseType)
    }

    private fun composeHttpEntity() {
        httpEntity = HttpEntity(body, headers)
    }

    abstract protected fun <T> build(type: Class<T>): ResponseEntity<T>
}