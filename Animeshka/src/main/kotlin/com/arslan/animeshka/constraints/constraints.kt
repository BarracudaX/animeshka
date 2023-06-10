package com.arslan.animeshka.constraints

import com.arslan.animeshka.UserRegistration
import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Constraint(validatedBy = [EqualRegistrationPasswordsConstraintValidator::class])
annotation class EqualRegistrationPasswords(val message: String = "",val groups: Array<KClass<*>> = [],val payload: Array<KClass<Payload>> = [])

@Component
class EqualRegistrationPasswordsConstraintValidator : ConstraintValidator<EqualRegistrationPasswords, UserRegistration>{

    override fun isValid(value: UserRegistration?, context: ConstraintValidatorContext?): Boolean {
        if(value == null) return true

        return value.password == value.repeatedPassword
    }


}