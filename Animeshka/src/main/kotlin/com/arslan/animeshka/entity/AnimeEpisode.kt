package com.arslan.animeshka.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.LocalDate

@Table("ANIME_EPISODES")
class AnimeEpisode(
    var episodeName: String,

    var animeId: Long,

    var aired: LocalDate,

    var synopsis: String,

    var score: BigDecimal? = null,

    @Id
    var id: Long? = null
)