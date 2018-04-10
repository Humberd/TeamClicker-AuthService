package com.teamclicker.authservice.controllers

import com.teamclicker.authservice.dao.Role.ADMIN
import com.teamclicker.authservice.dao.Role.USER
import com.teamclicker.authservice.dao.SpELRole._ADMIN
import com.teamclicker.authservice.dao.UserAccountDeletionDAO
import com.teamclicker.authservice.dao.UserRoleDAO
import com.teamclicker.authservice.dto.AccountUpdateRolesDTO
import com.teamclicker.authservice.exceptions.EntityDoesNotExistException
import com.teamclicker.authservice.exceptions.InvalidCredentialsException
import com.teamclicker.authservice.repositories.UserAccountRepository
import com.teamclicker.authservice.security.JWTData
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import mu.KLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@CrossOrigin
@RestController
@RequestMapping("/api/auth/accounts")
class AccountController(
    private val userAccountRepository: UserAccountRepository
) {

    @ApiOperation(
        value = "Deletes user account", notes = """
Every authenticated user can delete his own account.
Only ADMIN can delete any user account.
    """
    )
    @ApiResponses(
        value = [
            ApiResponse(code = 200, message = "Account deleted successfully"),
            ApiResponse(code = 401, message = "Invalid credentials"),
            ApiResponse(code = 403, message = "Unauthorized request"),
            ApiResponse(code = 411, message = "User does not exist")
        ]
    )
    @PreAuthorize("isAuthenticated()")
    @Transactional
    @DeleteMapping("/{accountId}/delete")
    fun deleteAccount(@PathVariable accountId: Long, jwt: JWTData): ResponseEntity<Void> {
        when {
            jwt.`is`(ADMIN) -> {
                logger.debug { "Deleting account $accountId as $ADMIN(${jwt.accountId})" }
            }
            jwt.`is`(USER) -> {
                if (accountId != jwt.accountId) {
                    throw InvalidCredentialsException("Cannot delete account of another user")
                }
            }
        }

        val account = userAccountRepository.findById(accountId)
        if (!account.isPresent) {
            logger.error {
                """Trying to delete a user, but the user does not exist.
                |Potential fix: Clear user token after account deletion.""".trimMargin()
            }
            throw EntityDoesNotExistException("User does not exist")
        }
        account.get().also {
            it.deletion = UserAccountDeletionDAO()
        }

        return ResponseEntity(HttpStatus.OK)
    }

    @ApiOperation(
        value = "Undeletes user account", notes = """
Only ADMIN can undelete any user account
        """
    )
    @ApiResponses(
        value = [
            ApiResponse(code = 200, message = "Account undeleted successfully"),
            ApiResponse(code = 401, message = "Invalid credentials"),
            ApiResponse(code = 403, message = "Unauthorized request"),
            ApiResponse(code = 411, message = "User does not exist")
        ]
    )
    @PreAuthorize("hasAuthority($_ADMIN)")
    @Transactional
    @PostMapping("/{accountId}/undelete")
    fun undeleteAccount(@PathVariable accountId: Long, jwt: JWTData): ResponseEntity<Void> {

        val account = userAccountRepository.findByIdNoConstraints(accountId)
        if (!account.isPresent) {
            logger.error { "Trying to undelete a user, but the user does not exist." }
            throw EntityDoesNotExistException("User does not exist")
        }

        account.get().also {
            it.deletion = null
        }

        return ResponseEntity(HttpStatus.OK)
    }

    @ApiOperation(
        value = "Updates user roles", notes = """
Only ADMIN can update any user roles
        """
    )
    @ApiResponses(
        value = [
            ApiResponse(code = 200, message = "Roles changed successfully"),
            ApiResponse(code = 400, message = "Invalid request body"),
            ApiResponse(code = 403, message = "Unauthorized request"),
            ApiResponse(code = 411, message = "User does not exist")
        ]
    )
    @PreAuthorize("hasAnyAuthority($_ADMIN)")
    @Transactional
    @PutMapping("/{accountId}/roles")
    fun updateRoles(
        @RequestBody @Valid body: AccountUpdateRolesDTO,
        @PathVariable accountId: Long,
        jwt: JWTData
    ): ResponseEntity<Void> {
        val userAccount = userAccountRepository.findById(accountId)
        if (!userAccount.isPresent) {
            throw EntityDoesNotExistException("User does not exist")
        }

        userAccount.get().also {
            it.roles = body.roles.map { UserRoleDAO(it) }.toSet()
        }
        return ResponseEntity(HttpStatus.OK)
    }


    companion object : KLogging()
}