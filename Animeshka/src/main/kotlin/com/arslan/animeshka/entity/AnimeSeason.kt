package com.arslan.animeshka.entity

import com.arslan.animeshka.Season
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("ANIME_SEASONS")
data class AnimeSeason(
    var season: Season,

    var year: Int,

    @Id
    val id: Long? = null
)