package com.teamclicker.authservice.repositories

import com.teamclicker.authservice.dao.UserAccountDAO
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserAccountRepository : JpaRepository<UserAccountDAO, Long> {
    @Query(
        """
            select user
            from UserAccountDAO as user
            where user.emailPasswordAuth.emailLc = :emailLc
            and user.deletion is null
        """
    )
    fun findByEmail(@Param("emailLc") emailLc: String): Optional<UserAccountDAO>

    // https://stackoverflow.com/a/12052390/4256929
    @Query(
        """
            select case when (count (user) > 0) then true else false end
            from UserAccountDAO as user
            where user.emailPasswordAuth.emailLc = :emailLc
            and user.deletion is null
        """
    )
    fun existsByEmail(@Param("emailLc") emailLc: String): Boolean

    @Query(
        """
            select user
            from UserAccountDAO as user
            where user.id = :id
            and user.deletion is null
        """
    )
    override fun findById(@Param("id") id: Long): Optional<UserAccountDAO>

    @Query(
        """
            select user
            from UserAccountDAO as user
            where user.id = :id
        """
    )
    fun findByIdNoConstraints(@Param("id") id: Long): Optional<UserAccountDAO>

    @Query(
        """
            select user
            from UserAccountDAO as user
            where user.emailPasswordAuth.passwordReset.token = :token
            and user.emailPasswordAuth.passwordReset.expiresAt >= :currentDate
            and user.deletion is null
        """
    )
    fun findByValidPasswordResetToken(
        @Param("token") token: String,
        @Param("currentDate") currentDate: Date
    ): Optional<UserAccountDAO>

}