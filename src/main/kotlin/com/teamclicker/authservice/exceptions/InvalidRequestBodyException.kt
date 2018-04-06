package com.teamclicker.authservice.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseBody
@ResponseStatus(HttpStatus.BAD_REQUEST) // 400
class InvalidRequestBodyException(message: String) : RuntimeException(message)