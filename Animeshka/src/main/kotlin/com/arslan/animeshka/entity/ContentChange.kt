package com.arslan.animeshka.entity

import com.arslan.animeshka.ContentChangeOperation
import com.arslan.animeshka.ContentChangeStatus
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("CONTENT_CHANGES")
data class ContentChange(
    @Column("content_id")
    val contentID: Long,

    @Column("property_path")
    val propertyPath: String,

    val operation: ContentChangeOperation,

    @Column("source_value")
    val sourceValue: String,

    val creatorID: Long,

    val timestamp: LocalDateTime = LocalDateTime.now(),

    @Column("moderator_id")
    val moderatorID: Long? = null,

    @Id
    val id: Long? = null
)