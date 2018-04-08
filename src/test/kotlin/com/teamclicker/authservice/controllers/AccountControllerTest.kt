@file:Suppress("RemoveRedundantBackticks")

package com.teamclicker.authservice.controllers

import com.teamclicker.authservice.Constants.JWT_HEADER_NAME
import com.teamclicker.authservice.controllers.helpers.AccountControllerHelper
import com.teamclicker.authservice.controllers.helpers.EmailPasswordAuthControllerHelper
import com.teamclicker.authservice.controllers.helpers.HttpConstants.ALICE
import com.teamclicker.authservice.controllers.helpers.HttpConstants.BOB
import com.teamclicker.authservice.controllers.helpers.HttpConstants.DAVE_ADMIN
import com.teamclicker.authservice.dao.Role.ADMIN
import com.teamclicker.authservice.dao.Role.USER
import com.teamclicker.authservice.dto.AccountUpdateRolesDTO
import com.teamclicker.authservice.repositories.UserAccountRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
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
        fun `should delete user own account and do not delete database records`() {
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

            val dbRecord = userAccountRepository.findByIdNoConstraints(aliceJwt.accountId)
            assertTrue(dbRecord.isPresent)
        }

        @Test
        fun `should not delete user account when its not his own`() {
            authHelper.signUp(ALICE)
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
        fun `should delete user account when its not his own, but requesting user is ADMIN`() {
            val aliceJwt = authHelper.signUp(ALICE)
            authHelper.signUp(DAVE_ADMIN)

            accountHelper.deleteAccount()
                .accountId(aliceJwt.accountId)
                .with(DAVE_ADMIN)
                .expectSuccess().also {
                    assertEquals(HttpStatus.OK, it.statusCode)
                    assertEquals(null, it.body)
                }

            authHelper.signIn()
                .with(ALICE)
                .expectError().also {
                    assertEquals(HttpStatus.valueOf(401), it.statusCode)
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

    @Nested
    inner class UndeleteAccount {
        @BeforeEach
        fun setUp() {
            userAccountRepository.deleteAll()
        }

        @Test
        fun `should undelete user account when user is ADMIN`() {
            val aliceJwt = authHelper.signUp(ALICE)
            authHelper.signUp(DAVE_ADMIN)

            accountHelper.deleteAccount()
                .accountId(aliceJwt.accountId)
                .with(DAVE_ADMIN)
                .expectSuccess().also {
                    assertEquals(HttpStatus.OK, it.statusCode)
                }

            authHelper.signIn()
                .with(ALICE)
                .expectError().also {
                    assertEquals(HttpStatus.valueOf(401), it.statusCode)
                }

            accountHelper.undeleteAccount()
                .accountId(aliceJwt.accountId)
                .with(DAVE_ADMIN)
                .expectSuccess().also {
                    assertEquals(HttpStatus.OK, it.statusCode)
                }

            authHelper.signIn()
                .with(ALICE)
                .expectSuccess().also {
                    assertEquals(HttpStatus.OK, it.statusCode)
                }
        }

        @Test
        fun `should not undlete user account when user is USER`() {
            val aliceJwt = authHelper.signUp(ALICE)
            authHelper.signUp(BOB)
            authHelper.signUp(DAVE_ADMIN)

            accountHelper.deleteAccount()
                .accountId(aliceJwt.accountId)
                .with(DAVE_ADMIN)
                .expectSuccess().also {
                    assertEquals(HttpStatus.OK, it.statusCode)
                }

            authHelper.signIn()
                .with(ALICE)
                .expectError().also {
                    assertEquals(HttpStatus.valueOf(401), it.statusCode)
                }

            accountHelper.undeleteAccount()
                .accountId(aliceJwt.accountId)
                .with(BOB) // undeleting as bob
                .expectError().also {
                    assertEquals(HttpStatus.valueOf(403), it.statusCode)
                }

            authHelper.signIn()
                .with(ALICE)
                .expectError().also {
                    assertEquals(HttpStatus.valueOf(401), it.statusCode)
                }
        }

        @Test
        fun `should not undelete user account when user does not exist`() {
            authHelper.signUp(DAVE_ADMIN)

            accountHelper.undeleteAccount()
                .accountId(-4321)
                .with(DAVE_ADMIN)
                .expectError().also {
                    assertEquals(HttpStatus.valueOf(411), it.statusCode)
                }
        }
    }

    @Nested
    inner class UpdateRoles {
        @BeforeEach
        fun setUp() {
            userAccountRepository.deleteAll()
        }

        @Test
        fun `should update user roles`() {
            authHelper.signUp(DAVE_ADMIN)
            val aliceJwt = authHelper.signUp(ALICE)

            val body = AccountUpdateRolesDTO().also {
                it.roles = setOf(ADMIN, USER)
            }
            accountHelper.updateRoles()
                .accountId(aliceJwt.accountId)
                .with(DAVE_ADMIN)
                .sending(body)
                .expectSuccess().also {
                    assertEquals(HttpStatus.OK, it.statusCode)
                    assertEquals(null, it.body)
                }

            authHelper.signIn(ALICE).also {
                assertEquals(setOf(ADMIN, USER), it.roles)
            }
        }

        @Test
        fun `should not update user roles when user is not ADMIN`() {
            authHelper.signUp(BOB)
            val aliceJwt = authHelper.signUp(ALICE)

            val body = AccountUpdateRolesDTO().also {
                it.roles = setOf(ADMIN, USER)
            }
            accountHelper.updateRoles()
                .accountId(aliceJwt.accountId)
                .with(BOB)
                .sending(body)
                .expectSuccess().also {
                    assertEquals(HttpStatus.valueOf(403), it.statusCode)
                }
        }

        @Test
        fun `should not update user roles when user with userId does not exist`() {
            authHelper.signUp(DAVE_ADMIN)
            authHelper.signUp(ALICE)

            val body = AccountUpdateRolesDTO().also {
                it.roles = setOf(ADMIN, USER)
            }
            accountHelper.updateRoles()
                .accountId(-4321)
                .with(DAVE_ADMIN)
                .sending(body)
                .expectError().also {
                    assertEquals(HttpStatus.valueOf(411), it.statusCode)
                }

            authHelper.signIn(ALICE).also {
                assertEquals(setOf(USER), it.roles)
            }
        }

        @Test
        fun `should not update roles when new role does not exist`() {
            authHelper.signUp(DAVE_ADMIN)
            val aliceJwt = authHelper.signUp(ALICE)

            val body = AccountUpdateRolesDTO().also {
                it.roles = setOf(ADMIN, USER, "INVALID_ROLE")
            }
            accountHelper.updateRoles()
                .accountId(aliceJwt.accountId)
                .with(DAVE_ADMIN)
                .sending(body)
                .expectSuccess().also {
                    assertEquals(HttpStatus.valueOf(400), it.statusCode)
                }

            authHelper.signIn(ALICE).also {
                assertEquals(setOf(USER), it.roles)
            }
        }

        @Test
        fun `should not update roles when new role does not exist v2`() {
            authHelper.signUp(DAVE_ADMIN)
            val aliceJwt = authHelper.signUp(ALICE)

            val body = AccountUpdateRolesDTO().also {
                it.roles = setOf("INVALID_ROLE")
            }
            accountHelper.updateRoles()
                .accountId(aliceJwt.accountId)
                .with(DAVE_ADMIN)
                .sending(body)
                .expectSuccess().also {
                    assertEquals(HttpStatus.valueOf(400), it.statusCode)
                }

            authHelper.signIn(ALICE).also {
                assertEquals(setOf(USER), it.roles)
            }
        }
    }
}
