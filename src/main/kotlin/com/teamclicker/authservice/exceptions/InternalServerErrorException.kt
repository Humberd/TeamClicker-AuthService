package com.teamclicker.authservice.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseBody
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 500
class InternalServerErrorException(message: String) : RuntimeException(message)
