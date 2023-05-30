package com.arslan.animeshka.entity

import com.arslan.animeshka.UnverifiedContentStatus
import com.arslan.animeshka.NewContentType
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("UNVERIFIED_NEW_CONTENT")
data class UnverifiedContent(
    @Column("creator_id")
    val creatorID: Long,

    @Column("content_type")
    val contentType: NewContentType,

    val content: String,

    @Column("content_key")
    val contentKey: String,

    @Column("content_status")
    val contentStatus: UnverifiedContentStatus = UnverifiedContentStatus.PENDING_VERIFICATION,

    val timestamp: LocalDateTime = LocalDateTime.now(),

    val verifier: Long? = null,

    @Column("rejection_reason")
    val rejectionReason: String? = null,

    @Id
    val id: Long? = null
)