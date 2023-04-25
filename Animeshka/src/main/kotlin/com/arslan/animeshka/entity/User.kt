package com.arslan.animeshka.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("USERS")
class User(
    var firstName: String,

    var lastName: String,

    var email: String,

    var password: String,

    var role: UserRole,

    @Id
    var id: Long? = null
)