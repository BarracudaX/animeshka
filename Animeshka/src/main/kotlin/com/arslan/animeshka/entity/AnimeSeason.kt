package com.arslan.animeshka.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("ANIME_SEASONS")
data class AnimeSeason(
    var season: Season,

    var year: Int,

    @Id
    val id: Long? = null
)