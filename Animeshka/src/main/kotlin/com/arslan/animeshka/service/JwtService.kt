package com.arslan.animeshka.service

import com.arslan.animeshka.entity.User


interface JwtService {

    fun createToken(user: User) : String

}