package com.teamclicker.authservice.validators;

import com.teamclicker.authservice.dao.RoleType
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import kotlin.reflect.KClass

/* https://stackoverflow.com/questions/49712645/spring-does-not-create-constraintvalidator */
@Constraint(validatedBy = [RolesValidator::class])
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.FIELD)
annotation class ValidateRoles(
    val message: String = "{com.teamclicker.authservice.validators.ValidateRoles.message}",
    val groups: Array<KClass<*>> = arrayOf(),
    val payload: Array<KClass<*>> = arrayOf()
)

class RolesValidator : ConstraintValidator<ValidateRoles, Collection<String>> {
    val allowedValues: List<String> = RoleType.values().map { it.name }

    override fun isValid(value: Collection<String>?, context: ConstraintValidatorContext?): Boolean {
        return allowedValues.containsAll(value!!)
    }
}

