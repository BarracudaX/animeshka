package com.arslan.animeshka.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

@Table("ANIME")
data class Anime(
    val title: String,

    val status: AnimeStatus,

    val rating: BpoRating,

    val studio: Long,

    val demographic: Demographic,

    val licensor: Long,

    val season: Long,

    val japaneseTitle: String,

    val animeType: AnimeType = AnimeType.UNKNOWN,

    val explicitGenre: ExplicitGenre? = null,

    val airingTime: LocalTime? = null,

    val airingDay: DayOfWeek? = null,

    val animeRank: Int? = null,

    val duration: Int? = null,

    val episodesCount: Int? = null,

    val score: BigDecimal? = null,

    val airedAt: LocalDate? = null,

    val finishedAt: LocalDate? = null,

    @Id
    val id: Long? = null
)