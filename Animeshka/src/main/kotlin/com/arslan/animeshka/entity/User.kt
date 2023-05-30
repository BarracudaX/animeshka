package com.arslan.animeshka.entity

import com.arslan.animeshka.UserRole
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("USERS")
data class User(

    @Column("first_name")
    var firstName: String,

    @Column("last_name")
    var lastName: String,

    var username: String,

    var email: String,

    var password: String,

    var role: UserRole,

    @Id
    val id: Long? = null
)