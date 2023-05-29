package com.arslan.animeshka.entity

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
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
    @Transient
    val id: Long? = null
)