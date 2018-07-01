package com.teamclicker.authservice.testConfig.extensions

import com.google.gson.Gson

fun <T> T.toJson(): String {
    return Gson().toJson(this)
}

fun <T> String.fromJson(type: Class<T>): T {
    return Gson().fromJson(this, type)
}