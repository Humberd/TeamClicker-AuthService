package com.teamclicker.authservice.dto

import com.teamclicker.authservice.Constants.MIN_PASSWORD_SIZE
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

class EPSignUpDTO {
    @NotBlank
    @Email
    var email: String? = null

    @NotBlank
    @Size(min = MIN_PASSWORD_SIZE)
    var password: String? = null
}
