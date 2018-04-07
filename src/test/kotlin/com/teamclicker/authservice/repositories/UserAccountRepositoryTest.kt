@file:Suppress("RemoveRedundantBackticks")

package com.teamclicker.authservice.repositories

import com.teamclicker.authservice.controllers.helpers.HttpConstants.ALICE
import com.teamclicker.authservice.controllers.helpers.HttpConstants.BOB
import com.teamclicker.authservice.testhelpers.UserAccountRepositoryHelper
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
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
    lateinit var repositoryHelper: UserAccountRepositoryHelper
    @BeforeAll
    fun beforeAll() {
        repositoryHelper = UserAccountRepositoryHelper(userAccountRepository)
    }

    @Nested
    inner class FindByEmail {
        @BeforeEach
        fun setUp() {
            userAccountRepository.deleteAll()
        }

        @Test
        fun `should get a UserAccount`() {
            repositoryHelper.add(ALICE)

            val result = userAccountRepository.findByEmail(ALICE.email!!)

            assertNotNull(result)
        }

        @Test
        fun `should not get a UserAccount when User with provided email does not exist`() {
            repositoryHelper.add(ALICE)

            val result = userAccountRepository.findByEmail(BOB.email!!)

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
            repositoryHelper.add(ALICE)

            val result = userAccountRepository.existsByEmail(ALICE.email!!)

            assertTrue(result)
        }

        @Test
        fun `should return fakse when User with provided email does not exist`() {
            repositoryHelper.add(ALICE)

            val result = userAccountRepository.existsByEmail(BOB.email!!)

            assertFalse(result)
        }
    }
}