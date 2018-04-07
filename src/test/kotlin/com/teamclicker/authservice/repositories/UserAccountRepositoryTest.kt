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

            assertTrue(result.isPresent)
        }

        @Test
        fun `should not get a UserAccount when User with provided email does not exist`() {
            repositoryHelper.add(ALICE)

            val result = userAccountRepository.findByEmail(BOB.email!!)

            assertFalse(result.isPresent)
        }

        @Test
        fun `should not get a UserAccount when the account is deleted`() {
            val account = repositoryHelper.add(ALICE)
            repositoryHelper.delete(account)

            val result = userAccountRepository.findByEmail(ALICE.email!!)

            assertFalse(result.isPresent)
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
        fun `should return false when User with provided email does not exist`() {
            repositoryHelper.add(ALICE)

            val result = userAccountRepository.existsByEmail(BOB.email!!)

            assertFalse(result)
        }

        @Test
        fun `should return false when the account is deleted`() {
            val account = repositoryHelper.add(ALICE)
            repositoryHelper.delete(account)

            val result = userAccountRepository.existsByEmail(ALICE.email!!)

            assertFalse(result)
        }
    }

    @Nested
    inner class FindById {
        @BeforeEach
        fun setUp() {
            userAccountRepository.deleteAll()
        }

        @Test
        fun `should get a UserAccount`() {
            val aliceAccount = repositoryHelper.add(ALICE)

            val result = userAccountRepository.findById(aliceAccount.id!!)

            assertTrue(result.isPresent)
        }

        @Test
        fun `should not get a UserAccount when User with provided id does not exist`() {
            val aliceAccount = repositoryHelper.add(ALICE)

            val result = userAccountRepository.findById(-123)

            assertFalse(result.isPresent)
        }

        @Test
        fun `should not get a UserAccount when account is deleted`() {
            val aliceAccount = repositoryHelper.add(ALICE)
            repositoryHelper.delete(aliceAccount)

            val result = userAccountRepository.findById(aliceAccount.id!!)

            assertFalse(result.isPresent)
        }
    }
}