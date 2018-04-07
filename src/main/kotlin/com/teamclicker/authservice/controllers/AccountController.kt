package com.teamclicker.authservice.controllers

import com.teamclicker.authservice.security.JWTData
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

@CrossOrigin
@RestController
@RequestMapping("/api/auth/accounts")
class AccountController {

    @ApiResponses(
        value = [
            ApiResponse(code = 200, message = "Account deleted successfully"),
            ApiResponse(code = 401, message = "Permissions not sufficient"),
            ApiResponse(code = 403, message = "Unauthorized request")
        ]
    )
    @PreAuthorize("isAuthenticated()")
    @Transactional
    @DeleteMapping("/{accountId}")
    fun delete(
        @PathVariable accountId: Long,
        jwtData: JWTData
    ): ResponseEntity<Void> {
        return ResponseEntity(HttpStatus.OK)
    }
}