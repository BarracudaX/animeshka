package com.arslan.animeshka.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("USERS")
class User(

    @Column("first_name")
    var firstName: String,

    @Column("last_name")
    var lastName: String,

    var username: String,

    var email: String,

    var password: String,

    var role: UserRole,

    @Id
    var id: Long? = null
)