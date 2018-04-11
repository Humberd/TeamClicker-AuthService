package com.teamclicker.authservice.controllers.helpers

import com.teamclicker.authservice.controllers.helpers.HttpConstants.DAVE_ADMIN
import com.teamclicker.authservice.dao.Role
import com.teamclicker.authservice.dao.UserRoleDAO
import com.teamclicker.authservice.dto.*
import com.teamclicker.authservice.repositories.UserAccountRepository
import com.teamclicker.authservice.testhelpers.JwtExtractorHelper
import com.teamclicker.authservice.testmodels.UserAccountMock
import org.junit.jupiter.api.Assertions
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class EmailPasswordAuthControllerHelper(
    private val http: TestRestTemplate,
    private val jwtExtractorHelper: JwtExtractorHelper,
    private val userAccountRepository: UserAccountRepository
) {
    fun signUp() = SignUpEndpointBuilder()

    /**
     * Signs up a [user] and returns its Jwt data
     */
    fun signUp(user: UserAccountMock) = signUp()
        .with(user)
        .expectSuccess().also {
            Assertions.assertEquals(HttpStatus.OK, it.statusCode)
        }.let {
            jwtExtractorHelper.getJwtData(it)
        }

    fun signIn() = SignInEndpointBuilder()
    fun signIn(user: UserAccountMock) = SignInEndpointBuilder()
        .with(user)
        .expectSuccess().also {
            Assertions.assertEquals(HttpStatus.OK, it.statusCode)
        }.let {
            jwtExtractorHelper.getJwtData(it)
        }

    fun changePassword() = ChangePasswordEndpointBuilder()
    fun sendPasswordResetEmail() = SendPasswordResetEmailBuilder()
    fun resetPassword() = ResetPasswordBuilder()

    /**************************************************************************/

    inner class SignUpEndpointBuilder :
        EndpointBuilder<SignUpEndpointBuilder, EPSignUpDTO, String>(String::class.java, http) {
        var user: UserAccountMock? = null

        override fun with(user: UserAccountMock?): SignUpEndpointBuilder {
            this.user = user
            sending(user?.toEmailPasswordSignUp())
            return this
        }

        override fun <T> build(
            httpEntity: HttpEntity<EPSignUpDTO>,
            responseBodyType: Class<T>
        ): ResponseEntity<T> {
            val response = http.postForEntity(
                "/api/auth/emailPassword/signUp",
                httpEntity,
                responseBodyType
            )

            if (user == DAVE_ADMIN) {
                setAdminRole()
            }

            return response
        }

        private fun setAdminRole() {
            val dbUser = userAccountRepository.findByEmail(user?.email?.toLowerCase()!!)
            dbUser.get().also {
                it.roles = setOf(UserRoleDAO(Role.ADMIN), UserRoleDAO(Role.USER))
            }
            userAccountRepository.save(dbUser.get())
        }
    }

    inner class SignInEndpointBuilder :
        EndpointBuilder<SignInEndpointBuilder, EPSignInDTO, String>(String::class.java, http) {
        override fun with(user: UserAccountMock?): SignInEndpointBuilder {
            sending(user?.toEmailPasswordSignIn())
            return this
        }

        override fun <T> build(
            httpEntity: HttpEntity<EPSignInDTO>,
            responseBodyType: Class<T>
        ): ResponseEntity<T> {
            return http.postForEntity(
                "/api/auth/emailPassword/signIn",
                httpEntity,
                responseBodyType
            )
        }
    }

    inner class ChangePasswordEndpointBuilder :
        EndpointBuilder<ChangePasswordEndpointBuilder, EPChangePasswordDTO, String>(String::class.java, http) {

        override fun <T> build(
            httpEntity: HttpEntity<EPChangePasswordDTO>,
            responseBodyType: Class<T>
        ): ResponseEntity<T> {
            return http.postForEntity(
                "/api/auth/emailPassword/changePassword",
                httpEntity,
                responseBodyType
            )
        }
    }

    inner class SendPasswordResetEmailBuilder :
        EndpointBuilder<SendPasswordResetEmailBuilder, EPSendPasswordResetEmailDTO, String>(String::class.java, http) {

        override fun <T> build(
            httpEntity: HttpEntity<EPSendPasswordResetEmailDTO>,
            responseBodyType: Class<T>
        ): ResponseEntity<T> {
            return http.postForEntity(
                "/api/auth/emailPassword/sendPasswordResetEmail",
                httpEntity,
                responseBodyType
            )
        }

    }

    inner class ResetPasswordBuilder :
        EndpointBuilder<ResetPasswordBuilder, EPResetPasswordDTO, String>(String::class.java, http) {
        override fun <T> build(
            httpEntity: HttpEntity<EPResetPasswordDTO>,
            responseBodyType: Class<T>
        ): ResponseEntity<T> {
            return http.postForEntity(
                "/api/auth/emailPassword/resetPassword",
                httpEntity,
                responseBodyType
            )
        }

    }
}