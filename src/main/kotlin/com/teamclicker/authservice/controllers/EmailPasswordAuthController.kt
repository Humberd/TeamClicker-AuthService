package com.teamclicker.authservice.controllers

import com.teamclicker.authservice.Constants.JWT_HEADER_NAME
import com.teamclicker.authservice.dao.EmailPasswordAuthDAO
import com.teamclicker.authservice.dao.UserAccountDAO
import com.teamclicker.authservice.dao.UserRoleDAO
import com.teamclicker.authservice.dto.EmailPasswordChangePasswordDTO
import com.teamclicker.authservice.dto.EmailPasswordSignInDTO
import com.teamclicker.authservice.dto.EmailPasswordSignUpDTO
import com.teamclicker.authservice.exceptions.EntityAlreadyExistsException
import com.teamclicker.authservice.exceptions.InvalidCredentialsException
import com.teamclicker.authservice.exceptions.InvalidRequestBodyException
import com.teamclicker.authservice.repositories.UserAccountRepository
import com.teamclicker.authservice.security.AuthenticationMethod
import com.teamclicker.authservice.security.JWTData
import com.teamclicker.authservice.security.JWTHelper
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import io.swagger.annotations.ResponseHeader
import mu.KLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@CrossOrigin
@RestController()
@RequestMapping("/api/auth/emailPassword")
class EmailPasswordAuthController(
    private val userAccountRepository: UserAccountRepository,
    private val jwtHelper: JWTHelper,
    private val bCryptPasswordEncoder: BCryptPasswordEncoder
) {

    @ApiOperation(
        value = "Creates a new User Account",
        notes = """Creates a new User Account and automatically signs the User in."""
    )
    @ApiResponses(
        value = [
            ApiResponse(
                code = 200, message = "Account created successfully", responseHeaders = [
                    ResponseHeader(name = JWT_HEADER_NAME, response = String::class)
                ]
            ),
            ApiResponse(code = 400, message = "Invalid request body"),
            ApiResponse(code = 410, message = "Account with provided email already exists")
        ]
    )
    @PreAuthorize("isAnonymous()")
    @Transactional
    @PostMapping("/signUp")
    fun signUp(@RequestBody @Valid body: EmailPasswordSignUpDTO): ResponseEntity<Void> {
        val userExists = userAccountRepository.existsByEmail(body.email?.toLowerCase()!!)
        if (userExists) {
            throw EntityAlreadyExistsException("User with this email already exists")
        }

        val newUserAccount = UserAccountDAO().also {
            it.emailPasswordAuth = EmailPasswordAuthDAO().also {
                it.email = body.email
                it.password = bCryptPasswordEncoder.encode(body.password)
            }
            it.roles = listOf(UserRoleDAO("USER"))
        }


        val savedUserAccount = userAccountRepository.save(newUserAccount)
        val jwtString =
            jwtHelper.convertUserAccountToJwtString(savedUserAccount, AuthenticationMethod.USERNAME_PASSWORD)
        val headers = jwtHelper.getHeaders(jwtString)

        return ResponseEntity(headers, HttpStatus.OK)
    }

    @ApiOperation(value = "Authenticates a User")
    @ApiResponses(
        value = [
            ApiResponse(
                code = 200, message = "Account created successfully", responseHeaders = [
                    ResponseHeader(name = JWT_HEADER_NAME, response = String::class)
                ]
            ),
            ApiResponse(code = 400, message = "Invalid request body"),
            ApiResponse(code = 401, message = "Invalid credentials")
        ]
    )
    @PreAuthorize("isAnonymous()")
    @PostMapping("/signIn")
    fun signIn(@RequestBody @Valid body: EmailPasswordSignInDTO): ResponseEntity<Void> {
        val userAccount = userAccountRepository.findByEmail(body.email!!.toLowerCase())
        if (userAccount === null) {
            logger.trace { "User not found" }
            throw InvalidCredentialsException("Invalid credentials")
        }

        val encodedPassword = userAccount.emailPasswordAuth?.password
        val rawPassword = body.password
        if (!bCryptPasswordEncoder.matches(rawPassword, encodedPassword)) {
            logger.trace { "Passwords don't match" }
            throw InvalidCredentialsException("Invalid credentials")
        }

        val jwtString = jwtHelper.convertUserAccountToJwtString(userAccount, AuthenticationMethod.USERNAME_PASSWORD)
        val headers = jwtHelper.getHeaders(jwtString)

        return ResponseEntity(headers, HttpStatus.OK)
    }

    @ApiOperation(value = "Changes User password")
    @ApiResponses(
        value = [
            ApiResponse(code = 200, message = "Password changed successfully"),
            ApiResponse(code = 400, message = "Invalid request body"),
            ApiResponse(code = 401, message = "Invalid credentials"),
            ApiResponse(code = 403, message = "Unauthorized request")
        ]
    )
    @Transactional
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/changePassword")
    fun changePassword(
        @RequestBody @Valid body: EmailPasswordChangePasswordDTO,
        jwtData: JWTData
    ): ResponseEntity<Void> {
        if (body.oldPassword == body.newPassword) {
            logger.trace { "Passwords are the same" }
            throw InvalidRequestBodyException("New password cannot be the same as the old password")
        }

        val userAccount = userAccountRepository.findById(jwtData.accountId)
        if (!userAccount.isPresent) {
            logger.error { "User ${jwtData.accountId} was authenticated, but couldn't be found in the database" }
            throw InvalidCredentialsException("Invalid credentials")
        }

        val encodedPassword = userAccount.get().emailPasswordAuth?.password
        if (!bCryptPasswordEncoder.matches(body.oldPassword, encodedPassword)) {
            logger.trace { "Passwords don't match" }
            throw InvalidCredentialsException("Invalid credentials")
        }

        userAccount.get().also {
            it.emailPasswordAuth.also {
                it?.password = bCryptPasswordEncoder.encode(body.newPassword)
            }
        }

        userAccountRepository.save(userAccount.get())

        return ResponseEntity(HttpStatus.OK)
    }

    companion object : KLogging()
}