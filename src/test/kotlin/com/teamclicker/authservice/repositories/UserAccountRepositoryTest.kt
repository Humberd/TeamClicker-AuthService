@file:Suppress("RemoveRedundantBackticks")

package com.teamclicker.authservice.repositories

import com.teamclicker.authservice.dao.EmailPasswordAuthDAO
import com.teamclicker.authservice.dao.UserAccountDAO
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class UserAccountRepositoryTest {
    @Autowired
    lateinit var userAccountRepository: UserAccountRepository

    @Nested
    inner class FindByEmail {
        @BeforeEach
        fun setUp() {
            userAccountRepository.deleteAll()
        }

        @Test
        fun `should get a UserAccount`() {
            userAccountRepository.saveAll(listOf(
                    UserAccountDAO().also {
                        it.emailPasswordAuth = EmailPasswordAuthDAO().also {
                            it.email = "alice@alice.com"
                            it.password = "alicePassword"
                        }
                    }
            ))

            val result = userAccountRepository.findByEmail("alice@alice.com")

            assertNotNull(result)
        }

        @Test
        fun `should not get a UserAccount when User with provided email does not exist`() {
            userAccountRepository.saveAll(listOf(
                    UserAccountDAO().also {
                        it.emailPasswordAuth = EmailPasswordAuthDAO().also {
                            it.email = "alice@alice.com"
                            it.password = "alicePassword"
                        }
                    }
            ))

            val result = userAccountRepository.findByEmail("bob@bob.com")

            assertNull(result)
        }
    }

    @Nested
    inner class ExistsByEmail {
        @BeforeEach
        fun setUp() {
            userAccountRepository.deleteAll()
        }

        @Test
        fun `should return true when User with provided email exists`() {
            userAccountRepository.saveAll(listOf(
                    UserAccountDAO().also {
                        it.emailPasswordAuth = EmailPasswordAuthDAO().also {
                            it.email = "alice@alice.com"
                            it.password = "alicePassword"
                        }
                    }
            ))

            val result = userAccountRepository.existsByEmail("alice@alice.com")

            assertTrue(result)
        }

        @Test
        fun `should return fakse when User with provided email does not exist`() {
            userAccountRepository.saveAll(listOf(
                    UserAccountDAO().also {
                        it.emailPasswordAuth = EmailPasswordAuthDAO().also {
                            it.email = "alice@alice.com"
                            it.password = "alicePassword"
                        }
                    }
            ))

            val result = userAccountRepository.existsByEmail("bobe@bob.com")

            assertFalse(result)
        }
    }
}