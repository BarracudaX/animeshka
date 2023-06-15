package com.arslan.animeshka.entity

import com.arslan.animeshka.CharacterRole
import kotlinx.serialization.Serializable
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Table

@Table("CHARACTERS")
@Serializable
data class Character(
    val characterName: String,

    val japaneseName: String? = null,

    val description: String = "",

    @Id
    val id: Long
) : Persistable<Long> {

    @kotlinx.serialization.Transient
    @Transient
    var isNewEntity: Boolean = false

    override fun getId(): Long = id

    override fun isNew(): Boolean = isNewEntity


}