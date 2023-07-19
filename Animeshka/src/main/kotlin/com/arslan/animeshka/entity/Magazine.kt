package com.arslan.animeshka.entity

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Table

@Table("NOVEL_MAGAZINES")
@Serializable
data class Magazine(
    val magazineName: String,

    @Id
    val id: Long
) : Persistable<Long> {

    @org.springframework.data.annotation.Transient
    @Transient
    var isNewEntity = false

    override fun getId(): Long = id

    override fun isNew(): Boolean = isNewEntity

}