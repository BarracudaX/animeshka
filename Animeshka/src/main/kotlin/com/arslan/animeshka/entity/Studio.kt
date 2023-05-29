package com.arslan.animeshka.entity

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate

@Table("STUDIOS")
data class Studio(
    val studioName: String,

    val japaneseName: String,

    val established: LocalDate,

    @Id
    val id: Long,

    @Transient
    val isNewEntity: Boolean = false
) : Persistable<Long> {
    override fun getId(): Long = id

    override fun isNew(): Boolean  = isNewEntity
}