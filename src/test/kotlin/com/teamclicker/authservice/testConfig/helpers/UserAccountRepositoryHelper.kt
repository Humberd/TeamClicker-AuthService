package com.teamclicker.authservice.testConfig.helpers

import com.teamclicker.authservice.dao.UserAccountDAO
import com.teamclicker.authservice.dao.UserAccountDeletionDAO
import com.teamclicker.authservice.repositories.UserAccountRepository
import com.teamclicker.authservice.testConfig.models.UserAccountMock

class UserAccountRepositoryHelper(private val userAccountRepository: UserAccountRepository) {
    fun add(userAccount: UserAccountMock): UserAccountDAO {
        return userAccountRepository.save(UserAccountDAO().also {
            it.emailPasswordAuth = userAccount.toEmailPasswordAuthDAO()
        })
    }

    fun delete(userAccountDAO: UserAccountDAO) {
        userAccountDAO.deletion = UserAccountDeletionDAO()
        userAccountRepository.save(userAccountDAO)
    }
}