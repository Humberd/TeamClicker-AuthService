package com.teamclicker.authservice.dto

import com.teamclicker.authservice.Constants
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

class EmailPasswordChangePasswordDTO {
    @NotBlank
    var oldPassword: String? = null

    @NotBlank
    @Size(min = Constants.MIN_PASSWORD_SIZE)
    var newPassword: String? = null
}