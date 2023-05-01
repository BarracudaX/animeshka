package com.arslan.animeshka.service

import com.arslan.animeshka.entity.User
import com.arslan.animeshka.entity.UserCredentials
import com.arslan.animeshka.entity.UserRegistration
import org.springframework.validation.annotation.Validated
import javax.validation.Valid

interface UserService {

    suspend fun register(userRegistration: UserRegistration) : User

    suspend fun login(userCredentials: UserCredentials) : String

}