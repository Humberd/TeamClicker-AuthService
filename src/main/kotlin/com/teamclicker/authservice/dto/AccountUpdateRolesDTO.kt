package com.teamclicker.authservice.dto

import com.teamclicker.authservice.validators.ValidateRoles
import javax.validation.constraints.NotNull

class AccountUpdateRolesDTO {
    @NotNull
    @field:ValidateRoles
    var roles: Set<String> = emptySet()
}