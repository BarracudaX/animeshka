package com.arslan.animeshka.entity

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

    @Column("from_value")
    val fromValue: String? = null,

    val creatorID: Long,

    val timestamp: LocalDateTime = LocalDateTime.now(),

    val acceptorID: Long? = null,

    @Id
    val id: Long? = null
)