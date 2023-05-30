package com.arslan.animeshka.entity

import com.arslan.animeshka.VerifiedContentStatus
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

/**
 * Represents verified content.
 */
@Table("VERIFIED_CONTENT")
data class VerifiedContent(

    @Column("content_status")
    val contentStatus: VerifiedContentStatus,

    @Id
    val id:Long
) : Persistable<Long> {
    @Transient
    var isNewEntity: Boolean = false

    override fun getId(): Long  = id

    override fun isNew(): Boolean = isNewEntity
}