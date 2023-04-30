package com.arslan.animeshka.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("CHARACTERS")
class Character(
    val characterName: String,

    val japaneseName: String? = null,

    val description: String = "",

    @Id
    val id: Long? = null
)