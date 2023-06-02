package com.arslan.animeshka.service

import com.arslan.animeshka.entity.User
import com.arslan.animeshka.UserCredentials
import com.arslan.animeshka.UserRegistration
import org.springframework.security.authentication.ReactiveAuthenticationManager

interface UserService : ReactiveAuthenticationManager{

    suspend fun register(userRegistration: UserRegistration) : User

}