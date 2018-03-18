package com.teamclicker.authservice.repositories

import com.teamclicker.authservice.dao.UserAccountDAO
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface UserAccountRepository : JpaRepository<UserAccountDAO, Long> {
    @Query("""
        select user
        from UserAccountDAO as user
        where user.emailPasswordAuth.emailLc = :emailLc
        """)
    fun findByEmail(@Param("emailLc") emailLc: String): UserAccountDAO?

    // https://stackoverflow.com/a/12052390/4256929
    @Query("""
        select case when (count (user) > 0) then true else false end
        from UserAccountDAO as user
        where user.emailPasswordAuth.emailLc = :emailLc
        """)
    fun existsByEmail(@Param("emailLc") emailLc: String): Boolean
}