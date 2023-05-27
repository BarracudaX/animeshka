package com.arslan.animeshka.service

import com.arslan.animeshka.entity.User
import com.arslan.animeshka.UserCredentials
import com.arslan.animeshka.UserRegistration

interface UserService {

    suspend fun register(userRegistration: UserRegistration) : User

    suspend fun login(userCredentials: UserCredentials) : String

}