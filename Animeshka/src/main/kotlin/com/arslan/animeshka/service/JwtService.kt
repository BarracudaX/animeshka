package com.arslan.animeshka.service

import com.arslan.animeshka.entity.User


interface JwtService {

    suspend fun createToken(user: User) : String

}