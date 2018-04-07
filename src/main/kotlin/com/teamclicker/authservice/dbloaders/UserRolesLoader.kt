package com.teamclicker.authservice.dbloaders

import com.teamclicker.authservice.dao.Role
import com.teamclicker.authservice.dao.UserRoleDAO
import com.teamclicker.authservice.repositories.UserRoleRepository
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

@Component
class UserRolesLoader(
    private val userRoleRepository: UserRoleRepository
) : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
        userRoleRepository.saveAll(
            listOf(
                UserRoleDAO(Role.USER),
                UserRoleDAO(Role.ADMIN)
            )
        )
    }
}