package com.arslan.animeshka.entity

import com.arslan.animeshka.ImageExtension
import com.arslan.animeshka.ImageType
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("IMAGES")
data class Image(
        @Column("content_id")
        val contentID: Long,

        @Column("image_type")
        val imageType: ImageType,

        @Column("image_extension")
        val imageExtension: ImageExtension,

        @Id val id: Long? = null
)