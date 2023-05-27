package com.arslan.animeshka.constraints

import com.arslan.animeshka.UserRegistration
import org.springframework.stereotype.Component
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
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