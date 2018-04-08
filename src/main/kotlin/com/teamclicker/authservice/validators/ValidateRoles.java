package com.teamclicker.authservice.validators;

import javax.validation.Constraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/* https://stackoverflow.com/questions/49712645/spring-does-not-create-constraintvalidator */
@Constraint(validatedBy = RolesValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ValidateRoles {
    String message() default "{com.teamclicker.authservice.validators.ValidateRoles.message}";

    Class[] groups() default {};

    Class[] payload() default {};
}
