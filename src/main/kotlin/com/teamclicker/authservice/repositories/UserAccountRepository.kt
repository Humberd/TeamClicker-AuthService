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

    //    @Query("""
//        select true
//        from UserAccountDAO as user
//        where exists (
//            select emailPasswordAuth
//            from user.emailPasswordAuth as emailPasswordAuth
//            where emailPasswordAuth.emailLc = :emailLc
//        )
//        """)
    fun existsByEmailPasswordAuth_EmailLc(@Param("emailLc") emailLc: String): Boolean
}