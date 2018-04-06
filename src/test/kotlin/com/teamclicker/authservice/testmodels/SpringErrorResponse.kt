package com.teamclicker.authservice.testmodels

data class SpringErrorResponse(
    val path: String = "",
    val error: String = "",
    val message: String = "",
    val errors: List<ErrorsItem>? = listOf(),
    val timestamp: String = "",
    val status: Int = 0
)


data class ErrorsItem(
    val codes: List<String>?,
    val bindingFailure: Boolean = false,
    val code: String = "",
    val field: String = "",
    val defaultMessage: String = "",
    val objectName: String = "",
    val rejectedValue: String = ""
)


