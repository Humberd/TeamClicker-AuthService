package com.teamclicker.authservice.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseBody
@ResponseStatus(HttpStatus.GONE) // 410
class EntityAlreadyExistsException(message: String) : RuntimeException(message)
