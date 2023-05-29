package com.arslan.animeshka.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.LocalDate

@Table("ANIME_EPISODES")
data class AnimeEpisode(

    @Column("episode_name")
    val episodeName: String,

    @Column("anime_id")
    val animeId: Long,

    val aired: LocalDate,

    val synopsis: String,

    val score: BigDecimal? = null,

    @Id
    val id: Long
)