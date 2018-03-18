package com.teamclicker.authservice.dto

import javax.validation.constraints.NotBlank

class EmailPasswordSignInDTO {
    @NotBlank
    var email: String? = null

    @NotBlank
    var password: String? = null
}