package com.arslan.animeshka.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

/**
 * Represents verified content.
 */
@Table("CONTENTS")
data class Content(

    @Id
    val id:Long ? = null
)