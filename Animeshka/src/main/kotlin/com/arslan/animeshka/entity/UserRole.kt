package com.arslan.animeshka.entity

import com.arslan.animeshka.Role
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("USER_ROLES")
data class UserRole(
        @Column("user_id")
        val userID: Long,

        @Column("user_role")
        val userRole: Role,

        @Id
        val id: Long? = null
)