package com.arslan.animeshka.entity

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("NOVEL_MAGAZINES")
@Serializable
data class Magazine(
    val magazineName: String,

    @Transient
    @Id
    val id: Long? = null
)