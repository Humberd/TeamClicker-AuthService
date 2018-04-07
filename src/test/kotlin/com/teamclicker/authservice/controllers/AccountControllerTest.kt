@file:Suppress("RemoveRedundantBackticks")

package com.teamclicker.authservice.controllers

import com.teamclicker.authservice.Constants.JWT_HEADER_NAME
import com.teamclicker.authservice.controllers.helpers.AccountControllerHelper
import com.teamclicker.authservice.controllers.helpers.EmailPasswordAuthControllerHelper
import com.teamclicker.authservice.controllers.helpers.HttpConstants.ALICE
import com.teamclicker.authservice.controllers.helpers.HttpConstants.BOB
import com.teamclicker.authservice.repositories.UserAccountRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class AccountControllerTest {
    @Autowired
    lateinit var userAccountRepository: UserAccountRepository
    @Autowired
    lateinit var authHelper: EmailPasswordAuthControllerHelper
    @Autowired
    lateinit var accountHelper: AccountControllerHelper

    @Nested
    inner class DeleteAccount {
        @BeforeEach
        fun setUp() {
            userAccountRepository.deleteAll()
        }

        @Test
        fun `should delete user own account`() {
            val aliceJwt = authHelper.signUp(ALICE)

            accountHelper.deleteAccount()
                .accountId(aliceJwt.accountId)
                .with(ALICE)
                .expectSuccess()
                .also {
                    assertEquals(HttpStatus.OK, it.statusCode)
                    assertEquals(null, it.body)
                }

            authHelper.signIn()
                .with(ALICE)
                .expectError()
                .also {
                    assertEquals(HttpStatus.valueOf(401), it.statusCode)
                }
        }

        @Test
        fun `should not delete user account when its not his own`() {
            val aliceJwt = authHelper.signUp(ALICE)
            val bobJwt = authHelper.signUp(BOB)

            accountHelper.deleteAccount()
                .accountId(bobJwt.accountId)
                .with(ALICE)
                .expectError()
                .also {
                    assertEquals(HttpStatus.valueOf(401), it.statusCode)
                }

            authHelper.signIn()
                .with(BOB)
                .expectSuccess()
                .also {
                    assertEquals(HttpStatus.OK, it.statusCode)
                }

        }

        @Test
        fun `should not delete own account when account is already deleted`() {
            val aliceJwt = authHelper.signUp(ALICE)

            accountHelper.deleteAccount()
                .accountId(aliceJwt.accountId)
                .with(ALICE)
                .expectSuccess()
                .also {
                    assertEquals(HttpStatus.OK, it.statusCode)
                    assertEquals(null, it.body)
                }
            accountHelper.deleteAccount()
                .accountId(aliceJwt.accountId)
                .addHeader(JWT_HEADER_NAME, aliceJwt.token)
                .expectError()
                .also {
                    assertEquals(HttpStatus.valueOf(411), it.statusCode)
                }
        }
    }
}
