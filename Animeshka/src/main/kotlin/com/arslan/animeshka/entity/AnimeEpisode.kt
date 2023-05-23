package com.arslan.animeshka.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.LocalDate

@Table("ANIME_EPISODES")
class AnimeEpisode(

    @Column("episode_name")
    var episodeName: String,

    @Column("anime_id")
    var animeId: Long,

    var aired: LocalDate,

    var synopsis: String,

    val isVerified: Boolean = false,

    var score: BigDecimal? = null,

    @Id
    var id: Long? = null
)