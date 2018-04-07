package com.teamclicker.authservice.controllers.helpers

import com.teamclicker.authservice.testmodels.UserAccountMock

object HttpConstants {
    val ALICE
        get() = UserAccountMock(
            email = "alice@alice.com",
            password = "alicePassword"
        )
    val BOB
        get() = UserAccountMock(
            email = "bobe@bob.com",
            password = "bobPassword"
        )
    val CHUCK
        get() = UserAccountMock(
            email = "chuck@chuck.com",
            password = "chuckPassword"
        )
    val ANONYMOUS: UserAccountMock?
        get() = null
}