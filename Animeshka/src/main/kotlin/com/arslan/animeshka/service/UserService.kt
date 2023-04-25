package com.arslan.animeshka.service

import com.arslan.animeshka.entity.User
import com.arslan.animeshka.entity.UserCredentials
import com.arslan.animeshka.entity.UserRegistration

interface UserService {

    suspend fun register(userRegistration: UserRegistration) : User

    suspend fun login(userCredentials: UserCredentials) : String

}