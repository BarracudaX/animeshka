package com.arslan.animeshka.entity

import com.arslan.animeshka.constraints.EqualRegistrationPasswords
import kotlinx.serialization.Serializable
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern

@Serializable
@EqualRegistrationPasswords(message = "{UserRegistration.EqualRegistrationPasswords.message}")
data class UserRegistration(

    @field:NotBlank(message = "{UserRegistration.email.NotBlank.message}")
    @field:Email(message = "{UserRegistration.email.Email.message}")
    val email : String,

    @field:Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@\$!%*#?&])[A-Za-z\\d@\$!%*#?&]{8,}\$", message = "{UserRegistration.password.Pattern.message}")
    val password: String,

    val repeatedPassword: String,

    @field:NotBlank(message = "{UserRegistration.username.NotBlank.message}")
    val username: String,

    @field:NotBlank(message = "{UserRegistration.firstName.NotBlank.message}")
    val firstName: String,

    @field:NotBlank(message = "{UserRegistration.lastName.NotBlank.message}")
    val lastName: String
)

@Serializable
data class UserCredentials(
    @field:NotBlank(message = "{UserCredentials.credentials.message}")
    val credentials: String,

    @field:NotBlank(message = "{UserCredentials.password.message}")
    val password: String
)