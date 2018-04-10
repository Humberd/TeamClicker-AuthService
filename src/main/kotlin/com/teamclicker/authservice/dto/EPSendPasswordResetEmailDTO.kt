package com.teamclicker.authservice.dto

import javax.validation.constraints.NotBlank

class EPSendPasswordResetEmailDTO {
    @NotBlank
    var email: String? = null
}