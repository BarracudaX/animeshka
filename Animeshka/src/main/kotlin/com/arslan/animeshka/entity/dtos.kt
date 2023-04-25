package com.arslan.animeshka.entity

import kotlinx.serialization.Serializable

@Serializable
data class UserRegistration(val email : String, val password: String, val firstName: String, val lastName: String)

@Serializable
data class UserCredentials(val email: String,val password: String)