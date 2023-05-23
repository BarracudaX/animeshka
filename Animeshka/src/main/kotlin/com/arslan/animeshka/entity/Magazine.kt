package com.arslan.animeshka.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("NOVEL_MAGAZINES")
data class Magazine(
    val magazineName: String,

    val isVerified: Boolean = false,

    @Id
    val id: Long? = null
)