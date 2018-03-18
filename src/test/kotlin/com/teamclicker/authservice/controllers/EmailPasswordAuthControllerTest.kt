@file:Suppress("RemoveRedundantBackticks")

package com.teamclicker.authservice.controllers

import com.teamclicker.authservice.Constants.JWT_HEADER_NAME
import com.teamclicker.authservice.helpers.AuthHelper
import com.teamclicker.authservice.helpers.AuthHelper.Companion.ALICE
import com.teamclicker.authservice.helpers.AuthHelper.Companion.BOB
import com.teamclicker.authservice.helpers.JwtExtractorHelper
import com.teamclicker.authservice.repositories.UserAccountRepository
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class EmailPasswordAuthControllerTest {
    @Autowired
    lateinit var http: TestRestTemplate
    @Autowired
    lateinit var userAccountRepository: UserAccountRepository
    @Autowired
    lateinit var jwtExtractorHelper: JwtExtractorHelper
    lateinit var authHelper: AuthHelper

    @BeforeAll
    fun beforeAll() {
        authHelper = AuthHelper(http)
    }

    @Nested
    inner class SignUp {
        @BeforeEach
        fun setUp() {
            userAccountRepository.deleteAll()
        }

        @Test
        fun `should signUp and have a default 'USER' role`() {
            val response = authHelper.signUp()
                    .with(ALICE)
                    .expectSuccess()
                    .also {
                        assertEquals(HttpStatus.OK, it.statusCode)
                        assertNotNull(it.headers.get(JWT_HEADER_NAME))
                    }

            val jwtData = jwtExtractorHelper.getJwtData(response)
            assertEquals(listOf("USER"), jwtData.roles)
        }

        @Test
        fun `should not signUp when email does not have a valid format`() {
            authHelper.signUp()
                    .with(ALICE.also { it.email = "notValidEmail" })
                    .expectError()
                    .also {
                        assertEquals(HttpStatus.BAD_REQUEST, it.statusCode)
                        assertNull(it.headers.get(JWT_HEADER_NAME))
                        assertEquals(1, it.body?.errors?.size)
                        assertEquals("email", it.body?.errors?.get(0)?.field)
                        assertEquals("Email", it.body?.errors?.get(0)?.code)
                    }
        }

        @Test
        fun `should not signUp when password length is smaller than 5`() {
            authHelper.signUp()
                    .with(ALICE.also { it.password = "1234" })
                    .expectError()
                    .also {
                        assertEquals(HttpStatus.BAD_REQUEST, it.statusCode)
                        assertNull(it.headers.get(JWT_HEADER_NAME))
                        assertEquals(1, it.body?.errors?.size)
                        assertEquals("password", it.body?.errors?.get(0)?.field)
                        assertEquals("Size", it.body?.errors?.get(0)?.code)
                    }
        }

        @Test
        fun `should not signUp when there is already another user with the same email`() {
            authHelper.signUp()
                    .with(ALICE)
                    .expectSuccess()
                    .also {
                        assertEquals(HttpStatus.OK, it.statusCode)
                    }

            authHelper.signUp()
                    .with(ALICE.also { it.password = "differentPassword" })
                    .expectError()
                    .also {
                        assertEquals(HttpStatus.GONE, it.statusCode)
                        assertNull(it.headers.get(JWT_HEADER_NAME))
                    }
        }
    }

    @Nested
    inner class SignIn {
        @BeforeEach
        fun setUp() {
            userAccountRepository.deleteAll()
        }

        @Test
        fun `should signIn`() {
            authHelper.signIn()
                    .with(ALICE)
                    .expectSuccess()
                    .also {
                        assertEquals(HttpStatus.OK, it.statusCode)
                        assertNotNull(it.headers.get(JWT_HEADER_NAME))
                    }
        }

        @Test
        fun `should return different tokens for different users`() {
            val firstJwt = authHelper.signIn()
                    .with(ALICE)
                    .expectSuccess()
                    .also {
                        assertEquals(HttpStatus.OK, it.statusCode)
                        assertNotNull(it.headers.get(JWT_HEADER_NAME))
                    }.let {
                        it.headers.get(JWT_HEADER_NAME)?.first()
                    }

            val secondJwt = authHelper.signIn()
                    .with(BOB)
                    .expectSuccess()
                    .also {
                        assertEquals(HttpStatus.OK, it.statusCode)
                        assertNotNull(it.headers.get(JWT_HEADER_NAME))
                    }.let {
                        it.headers.get(JWT_HEADER_NAME)?.first()
                    }

            assertNotEquals(firstJwt, secondJwt)
        }

        @Test
        fun `should return different tokens for the same user`() {
            val firstJwt = authHelper.signIn()
                    .with(ALICE)
                    .expectSuccess()
                    .also {
                        assertEquals(HttpStatus.OK, it.statusCode)
                        assertNotNull(it.headers.get(JWT_HEADER_NAME))
                    }.let {
                        it.headers.get(JWT_HEADER_NAME)?.first()
                    }

            val secondJwt = authHelper.signIn()
                    .with(ALICE)
                    .expectSuccess()
                    .also {
                        assertEquals(HttpStatus.OK, it.statusCode)
                        assertNotNull(it.headers.get(JWT_HEADER_NAME))
                    }.let {
                        it.headers.get(JWT_HEADER_NAME)?.first()
                    }

            assertNotEquals(firstJwt, secondJwt)
        }

        @Test
        fun `should not signIn when providing invalid password for existing user`() {
            authHelper.signUp()
                    .with(ALICE)
                    .expectSuccess()
                    .also {
                        assertEquals(HttpStatus.OK, it.statusCode)
                    }

            authHelper.signIn()
                    .with(ALICE.also { it.password = "differentPassword123" })
                    .expectError()
                    .also {
                        assertEquals(HttpStatus.UNAUTHORIZED, it.statusCode)
                    }
        }

        @Test
        fun `should not signIn when providing credential fornot existing user`() {
            authHelper.signUp()
                    .with(ALICE)
                    .expectSuccess()
                    .also {
                        assertEquals(HttpStatus.OK, it.statusCode)
                    }

            authHelper.signIn()
                    .with(ALICE.also {
                        it.email = "differentMail.mail.com"
                        it.password = "differentPassword123"
                    })
                    .expectError()
                    .also {
                        assertEquals(HttpStatus.UNAUTHORIZED, it.statusCode)
                    }
        }
    }
}