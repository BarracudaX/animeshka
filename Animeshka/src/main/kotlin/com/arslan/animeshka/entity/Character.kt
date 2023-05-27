package com.arslan.animeshka.entity

import kotlinx.serialization.Serializable
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("CHARACTERS")
@Serializable
data class Character(
    val characterName: String,

    val japaneseName: String? = null,

    val description: String = "",

    val characterRole: CharacterRole,

    @Id
    val id: Long? = null
)