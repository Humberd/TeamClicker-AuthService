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
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
abstract class EndpointBuilder<Child, Body, Response>(
    val responseType: Class<Response>,
    val http: TestRestTemplate
) {
    protected val urlParams = hashMapOf<String, Any>()
    protected var body: Body? = null
    protected val headers = HttpHeaders()

    /**
     * Makes a http request to a signIn endpoint and saves its jwt to the headers map
     */
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

    fun addParam(key: String, value: Any): Child {
        this.urlParams.put(key, value)
        return this as Child
    }

    fun addHeader(key: String, value: String): Child {
        this.headers.add(key, value)
        return this as Child
    }

    fun <Err : Any> expectError(type: KClass<Err>): ResponseEntity<Err> {
        val httpEntity = composeHttpEntity()
        return build(httpEntity, type.java)
    }

    fun expectError(): ResponseEntity<SpringErrorResponse> {
        val httpEntity = composeHttpEntity()
        return build(httpEntity, SpringErrorResponse::class.java)
    }

    fun expectSuccess(): ResponseEntity<Response> {
        val httpEntity = composeHttpEntity()
        return build(httpEntity, responseType)
    }

    private fun composeHttpEntity(): HttpEntity<Body> {
        return HttpEntity(body, headers)
    }

    abstract protected fun <T> build(httpEntity: HttpEntity<Body>, responseBodyType: Class<T>): ResponseEntity<T>
}