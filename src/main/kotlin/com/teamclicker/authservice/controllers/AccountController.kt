package com.teamclicker.authservice.controllers

import com.teamclicker.authservice.dao.UserAccountDeletionDAO
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

@CrossOrigin
@RestController
@RequestMapping("/api/auth/accounts")
class AccountController(
    private val userAccountRepository: UserAccountRepository
) {

    @ApiOperation(
        value = "Deletes user account", notes = """

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
    fun deleteAccount(
        @PathVariable accountId: Long,
        jwt: JWTData
    ): ResponseEntity<Void> {
        if (accountId != jwt.accountId) {
            throw InvalidCredentialsException("Cannot delete account of another user")
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


    companion object : KLogging()
}