package com.arslan.animeshka.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("IMAGE_ID_GENERATOR")
data class Image(@Id val id: Long? = null)