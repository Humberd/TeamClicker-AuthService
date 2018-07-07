package com.teamclicker.authservice.testConfig.extensions

import com.nhaarman.mockito_kotlin.whenever
import java.util.concurrent.CountDownLatch

fun <T> waitForCall(methodCall: T): CountDownLatch {
    val latch = CountDownLatch(1)

    whenever(methodCall).then {
        latch.countDown()
    }

    return latch
}