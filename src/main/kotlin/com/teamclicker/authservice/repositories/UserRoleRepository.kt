package com.teamclicker.authservice.repositories

import com.teamclicker.authservice.dao.UserRoleDAO
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRoleRepository : JpaRepository<UserRoleDAO, String>