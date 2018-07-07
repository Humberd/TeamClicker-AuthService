package com.teamclicker.authservice.controllers

import com.teamclicker.authservice.Constants.JWT_HEADER_NAME
import com.teamclicker.authservice.Constants.MIN_PASSWORD_SIZE
import com.teamclicker.authservice.controllers.helpers.EmailPasswordAuthControllerHelper
import com.teamclicker.authservice.controllers.helpers.HttpConstants.ALICE
import com.teamclicker.authservice.controllers.helpers.HttpConstants.ANONYMOUS
import com.teamclicker.authservice.controllers.helpers.HttpConstants.BOB
import com.teamclicker.authservice.controllers.helpers.HttpConstants.DAVE_ADMIN
import com.teamclicker.authservice.dto.EPChangePasswordDTO
import com.teamclicker.authservice.dto.EPResetPasswordDTO
import com.teamclicker.authservice.dto.EPSendPasswordResetEmailDTO
import com.teamclicker.authservice.kafka.dto.PasswordResetEmailKDTO
import com.teamclicker.authservice.repositories.UserAccountRepository
import com.teamclicker.authservice.services.HashingService
import com.teamclicker.authservice.testConfig.helpers.JwtExtractorHelper
import com.teamclicker.authservice.testConfig.kafka.KafkaMockConsumer
import mu.KLogging
import org.junit.jupiter.api.Assertions.*
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
internal class EmailPasswordAuthControllerTest {
    @Autowired
    lateinit var userAccountRepository: UserAccountRepository
    @Autowired
    lateinit var hashingService: HashingService
    @Autowired
    lateinit var jwtExtractorHelper: JwtExtractorHelper
    @Autowired
    lateinit var authHelper: EmailPasswordAuthControllerHelper
    @Autowired
    lateinit var kafkaMockConsumer: KafkaMockConsumer

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
                    assertNotNull(it.headers[JWT_HEADER_NAME])
                }

            val jwtData = jwtExtractorHelper.getJwtData(response)
            assertEquals(setOf("USER"), jwtData.roles)
        }

        @Test
        fun `should not signUp when email does not have a valid format`() {
            authHelper.signUp()
                .with(ALICE.also { it.email = "notValidEmail" })
                .expectError()
                .also {
                    assertEquals(HttpStatus.BAD_REQUEST, it.statusCode)
                    assertNull(it.headers[JWT_HEADER_NAME])
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
                    assertNull(it.headers[JWT_HEADER_NAME])
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
                    assertNull(it.headers[JWT_HEADER_NAME])
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
            authHelper.signUp(ALICE)

            authHelper.signIn()
                .with(ALICE)
                .expectSuccess()
                .also {
                    assertEquals(HttpStatus.OK, it.statusCode)
                    assertNotNull(it.headers[JWT_HEADER_NAME])
                }
        }

        @Test
        fun `should return different tokens for different users`() {
            authHelper.signUp(ALICE)
            authHelper.signUp(BOB)

            val firstJwt = authHelper.signIn()
                .with(ALICE)
                .expectSuccess()
                .also {
                    assertEquals(HttpStatus.OK, it.statusCode)
                    assertNotNull(it.headers[JWT_HEADER_NAME])
                }.let {
                    it.headers[JWT_HEADER_NAME]?.first()
                }

            val secondJwt = authHelper.signIn()
                .with(BOB)
                .expectSuccess()
                .also {
                    assertEquals(HttpStatus.OK, it.statusCode)
                    assertNotNull(it.headers[JWT_HEADER_NAME])
                }.let {
                    it.headers[JWT_HEADER_NAME]?.first()
                }

            assertNotEquals(firstJwt, secondJwt)
        }

        @Test
        fun `should return different tokens for the same user`() {
            authHelper.signUp(ALICE)

            val firstJwt = authHelper.signIn()
                .with(ALICE)
                .expectSuccess()
                .also {
                    assertEquals(HttpStatus.OK, it.statusCode)
                    assertNotNull(it.headers[JWT_HEADER_NAME])
                }.let {
                    it.headers[JWT_HEADER_NAME]?.first()
                }

            val secondJwt = authHelper.signIn()
                .with(ALICE)
                .expectSuccess()
                .also {
                    assertEquals(HttpStatus.OK, it.statusCode)
                    assertNotNull(it.headers[JWT_HEADER_NAME])
                }.let {
                    it.headers[JWT_HEADER_NAME]?.first()
                }

            assertNotEquals(firstJwt, secondJwt)
        }

        @Test
        fun `should not signIn when providing invalid password for existing user`() {
            authHelper.signUp(ALICE)

            authHelper.signIn()
                .with(ALICE.also { it.password = "differentPassword123" })
                .expectError()
                .also {
                    assertEquals(HttpStatus.UNAUTHORIZED, it.statusCode)
                }
        }

        @Test
        fun `should not signIn when providing credential fornot existing user`() {
            authHelper.signUp(ALICE)

            authHelper.signIn()
                .with(BOB)
                .expectError()
                .also {
                    assertEquals(HttpStatus.UNAUTHORIZED, it.statusCode)
                }
        }
    }

    @Nested
    inner class ChangePassword {
        @BeforeEach
        fun setUp() {
            userAccountRepository.deleteAll()
        }

        @Test
        fun `should changePassword`() {
            authHelper.signUp(ALICE)

            val body = EPChangePasswordDTO().also {
                it.oldPassword = ALICE.password
                it.newPassword = "newAlicePassword"
            }
            authHelper.changePassword()
                .with(ALICE)
                .sending(body)
                .expectSuccess().also {
                    assertEquals(HttpStatus.OK, it.statusCode)
                    assertEquals(null, it.body)
                }

            authHelper.signIn()
                .with(ALICE.copy(password = "newAlicePassword"))
                .expectSuccess().also {
                    assertEquals(HttpStatus.OK, it.statusCode)
                }
        }

        @Test
        fun `should not changePassword when new password is the same as the old one`() {
            authHelper.signUp(ALICE)

            val body = EPChangePasswordDTO().also {
                it.oldPassword = ALICE.password
                it.newPassword = ALICE.password
            }
            authHelper.changePassword()
                .with(ALICE)
                .sending(body)
                .expectError().also {
                    assertEquals(HttpStatus.BAD_REQUEST, it.statusCode)
                }
        }

        @Test
        fun `should not changePassword when old password is invalid`() {
            authHelper.signUp(ALICE)

            val body = EPChangePasswordDTO().also {
                it.oldPassword = "invalidPassword"
                it.newPassword = "newAlicePassword"
            }
            authHelper.changePassword()
                .with(ALICE)
                .sending(body)
                .expectError().also {
                    assertEquals(HttpStatus.UNAUTHORIZED, it.statusCode)
                }
        }
    }

    companion object : KLogging()

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class SendPasswordResetEmail {
        @BeforeEach
        fun setUp() {
            userAccountRepository.deleteAll()
            kafkaMockConsumer.clearRecords()
        }

        @Test
        fun `should send email as ANONYMOUS`() {
            authHelper.signUp(ALICE)

            val body = EPSendPasswordResetEmailDTO().also {
                it.email = ALICE.email?.toLowerCase()
            }
            authHelper.sendPasswordResetEmail()
                .with(ANONYMOUS)
                .sending(body)
                .expectSuccess()
                .also {
                    assertEquals(HttpStatus.OK, it.statusCode)
                    assertEquals(null, it.body)
                }

            val aliceAccount = userAccountRepository.findByEmail(ALICE.email?.toLowerCase()!!).get()
            val kafkaMessage = kafkaMockConsumer.getLatestMessageAs(PasswordResetEmailKDTO::class)
            assertEquals(
                hashingService.hashBySHA_256(kafkaMessage.token),
                aliceAccount.emailPasswordAuth?.passwordReset?.token
            )
        }

        @Test
        fun `should send email as ANONYMOUS and override previously saved token`() {
            authHelper.signUp(ALICE)

            var body = EPSendPasswordResetEmailDTO().also {
                it.email = ALICE.email?.toLowerCase()
            }
            authHelper.sendPasswordResetEmail()
                .with(ANONYMOUS)
                .sending(body)
                .expectSuccess()
                .also {
                    assertEquals(HttpStatus.OK, it.statusCode)
                    assertEquals(null, it.body)
                }

            kafkaMockConsumer.waitForMessage()
            var aliceAccount = userAccountRepository.findByEmail(ALICE.email?.toLowerCase()!!).get()
            val token1 = aliceAccount.emailPasswordAuth?.passwordReset?.token!!
            assert(token1.length > 0)

            body = EPSendPasswordResetEmailDTO().also {
                it.email = ALICE.email?.toLowerCase()
            }
            authHelper.sendPasswordResetEmail()
                .with(ANONYMOUS)
                .sending(body)
                .expectSuccess()
                .also {
                    assertEquals(HttpStatus.OK, it.statusCode)
                    assertEquals(null, it.body)
                }

            kafkaMockConsumer.waitForMessage()
            aliceAccount = userAccountRepository.findByEmail(ALICE.email?.toLowerCase()!!).get()
            val token2 = aliceAccount.emailPasswordAuth?.passwordReset?.token!!
            assert(token2.length > 0)

            assertNotEquals(token1, token2)
        }

        @Test
        fun `should not send email when requesting as USER`() {
            authHelper.signUp(ALICE)

            val body = EPSendPasswordResetEmailDTO().also {
                it.email = ALICE.email?.toLowerCase()
            }
            authHelper.sendPasswordResetEmail()
                .with(ALICE)
                .sending(body)
                .expectError()
                .also {
                    assertEquals(HttpStatus.valueOf(403), it.statusCode)
                }
        }

        @Test
        fun `should not send email when account with email does not exist`() {
            authHelper.signUp(ALICE)

            val body = EPSendPasswordResetEmailDTO().also {
                it.email = "notExisting@mail.com"
            }
            authHelper.sendPasswordResetEmail()
                .with(ANONYMOUS)
                .sending(body)
                .expectError()
                .also {
                    assertEquals(HttpStatus.valueOf(411), it.statusCode)
                }
        }

    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class ResetPassword {
        @BeforeEach
        fun setUp() {
            userAccountRepository.deleteAll()
            kafkaMockConsumer.clearRecords()
        }

        @Test
        fun `should reset a password`() {
            authHelper.signUp(ALICE)

            val body1 = EPSendPasswordResetEmailDTO().also {
                it.email = ALICE.email?.toLowerCase()
            }
            authHelper.sendPasswordResetEmail()
                .with(ANONYMOUS)
                .sending(body1)
                .expectSuccess().also {
                    assertEquals(HttpStatus.OK, it.statusCode)
                }

            val kafkaMessage = kafkaMockConsumer.getLatestMessageAs(PasswordResetEmailKDTO::class)
            assertEquals(kafkaMessage.email, ALICE.email?.toLowerCase())

            val body2 = EPResetPasswordDTO().also {
                it.newPassword = "admin123"
                it.token = kafkaMessage.token
            }
            authHelper.resetPassword()
                .with(ANONYMOUS)
                .sending(body2)
                .expectSuccess().also {
                    assertEquals(HttpStatus.OK, it.statusCode)
                }

            authHelper.signIn()
                .with(ALICE.also { it.password = "admin123" })
                .expectSuccess().also {
                    assertEquals(HttpStatus.OK, it.statusCode)
                }
        }

        @Test
        fun `should not reset a password when provided token is invalid`() {
            authHelper.signUp(ALICE)

            val body1 = EPSendPasswordResetEmailDTO().also {
                it.email = ALICE.email?.toLowerCase()
            }
            authHelper.sendPasswordResetEmail()
                .with(ANONYMOUS)
                .sending(body1)
                .expectSuccess().also {
                    assertEquals(HttpStatus.OK, it.statusCode)
                }

            val kafkaMessage = kafkaMockConsumer.getLatestMessageAs(PasswordResetEmailKDTO::class)
            assertEquals(kafkaMessage.email, ALICE.email?.toLowerCase())

            val body2 = EPResetPasswordDTO().also {
                it.newPassword = "admin123"
                it.token = "defenitelyInvalidToken"
            }
            authHelper.resetPassword()
                .with(ANONYMOUS)
                .sending(body2)
                .expectSuccess().also {
                    assertEquals(HttpStatus.valueOf(411), it.statusCode)
                }

            authHelper.signIn()
                .with(ALICE.also { it.password = "admin123" })
                .expectSuccess().also {
                    assertEquals(HttpStatus.valueOf(401), it.statusCode)
                }
        }

        @Test
        fun `should not reset password when requesting as USER`() {
            authHelper.signUp(ALICE)

            val body1 = EPSendPasswordResetEmailDTO().also {
                it.email = ALICE.email?.toLowerCase()
            }
            authHelper.sendPasswordResetEmail()
                .with(ANONYMOUS)
                .sending(body1)
                .expectSuccess().also {
                    assertEquals(HttpStatus.OK, it.statusCode)
                }

            val kafkaMessage = kafkaMockConsumer.getLatestMessageAs(PasswordResetEmailKDTO::class)
            assertEquals(kafkaMessage.email, ALICE.email?.toLowerCase())

            val body2 = EPResetPasswordDTO().also {
                it.newPassword = "admin123"
                it.token = kafkaMessage.token
            }
            authHelper.resetPassword()
                .with(ALICE)
                .sending(body2)
                .expectSuccess().also {
                    assertEquals(HttpStatus.valueOf(403), it.statusCode)
                }

            authHelper.signIn()
                .with(ALICE.also { it.password = "admin123" })
                .expectSuccess().also {
                    assertEquals(HttpStatus.valueOf(401), it.statusCode)
                }
        }

        @Test
        fun `should not reset password when requesting as ADMIN`() {
            authHelper.signUp(ALICE)
            authHelper.signUp(DAVE_ADMIN)

            val body1 = EPSendPasswordResetEmailDTO().also {
                it.email = ALICE.email?.toLowerCase()
            }
            authHelper.sendPasswordResetEmail()
                .with(ANONYMOUS)
                .sending(body1)
                .expectSuccess().also {
                    assertEquals(HttpStatus.OK, it.statusCode)
                }

            val kafkaMessage = kafkaMockConsumer.getLatestMessageAs(PasswordResetEmailKDTO::class)
            assertEquals(kafkaMessage.email, ALICE.email?.toLowerCase())

            val body2 = EPResetPasswordDTO().also {
                it.newPassword = "admin123"
                it.token = kafkaMessage.token
            }
            authHelper.resetPassword()
                .with(DAVE_ADMIN)
                .sending(body2)
                .expectSuccess().also {
                    assertEquals(HttpStatus.valueOf(403), it.statusCode)
                }

            authHelper.signIn()
                .with(ALICE.also { it.password = "admin123" })
                .expectSuccess().also {
                    assertEquals(HttpStatus.valueOf(401), it.statusCode)
                }
        }

        @Test
        fun `should not reset password when new password is too short`() {
            authHelper.signUp(ALICE)

            val body1 = EPSendPasswordResetEmailDTO().also {
                it.email = ALICE.email?.toLowerCase()
            }
            authHelper.sendPasswordResetEmail()
                .with(ANONYMOUS)
                .sending(body1)
                .expectSuccess().also {
                    assertEquals(HttpStatus.OK, it.statusCode)
                }

            val kafkaMessage = kafkaMockConsumer.getLatestMessageAs(PasswordResetEmailKDTO::class)
            assertEquals(kafkaMessage.email, ALICE.email?.toLowerCase())

            val body2 = EPResetPasswordDTO().also {
                it.newPassword = "admin123".substring(0..MIN_PASSWORD_SIZE)
                it.token = kafkaMessage.token
            }
            authHelper.resetPassword()
                .with(ANONYMOUS)
                .sending(body2)
                .expectSuccess().also {
                    assertEquals(HttpStatus.OK, it.statusCode)
                }

            authHelper.signIn()
                .with(ALICE.also { it.password = body2.newPassword })
                .expectSuccess().also {
                    assertEquals(HttpStatus.OK, it.statusCode)
                }
        }

    }
}