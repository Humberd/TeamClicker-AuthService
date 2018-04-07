package com.teamclicker.authservice.testhelpers

import com.teamclicker.authservice.controllers.helpers.EmailPasswordAuthControllerHelper
import com.teamclicker.authservice.testmodels.UserAccountMock
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus

class AuthHelper(http: TestRestTemplate) {
    val emailPasswordAuthControllerHelper = EmailPasswordAuthControllerHelper(http)

    fun signUp() = emailPasswordAuthControllerHelper.SignUpEndpointBuilder()
    fun signUp(user: UserAccountMock) = signUp()
        .with(user)
        .expectSuccess().also {
            assertEquals(HttpStatus.OK, it.statusCode)
        }

    fun signIn() = emailPasswordAuthControllerHelper.SignInEndpointBuilder()

    fun changePassword() = emailPasswordAuthControllerHelper.ChangePasswordEndpointBuilder()
}