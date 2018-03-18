package com.teamclicker.authservice.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseBody()
@ResponseStatus(HttpStatus.UNAUTHORIZED) // 401
class InvalidCredentialsException(message: String) : RuntimeException(message)
