package com.arslan.animeshka.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("NOVEL_MAGAZINES")
class Magazine(
    val magazineName: String,

    @Id
    val id: Long? = null
)