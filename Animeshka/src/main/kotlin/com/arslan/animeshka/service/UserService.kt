package com.arslan.animeshka.service

import com.arslan.animeshka.entity.User
import com.arslan.animeshka.UserCredentials
import com.arslan.animeshka.UserRegistration
import org.springframework.security.authentication.ReactiveAuthenticationManager

interface UserService : ReactiveAuthenticationManager{

    /**
     * Registers new user in the system.
     * @throws org.springframework.dao.DuplicateKeyException if user with provided email and/or username already exists.
     * @return saved user.
     */
    suspend fun register(userRegistration: UserRegistration) : User

}