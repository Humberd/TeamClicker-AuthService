package com.teamclicker.authservice.validators

import com.teamclicker.authservice.dao.RoleType
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

class RolesValidator : ConstraintValidator<ValidateRoles, Collection<String>> {
    val allowedValues: List<String> = RoleType.values().map { it.name }

    override fun isValid(value: Collection<String>?, context: ConstraintValidatorContext?): Boolean {
        return allowedValues.containsAll(value!!)
    }
}