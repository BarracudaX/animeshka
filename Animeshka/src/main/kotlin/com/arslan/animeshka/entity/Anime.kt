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

    @Column("anime_status")
    val status: AnimeStatus,

    val rating: SeriesRating,

    val studio: Long,

    val demographic: Demographic,

    val licensor: Long,

    val season: Long,

    @Column("japanese_title")
    val japaneseTitle: String,

    val synopsis: String,

    val animeType: AnimeType = AnimeType.UNKNOWN,

    val explicitGenre: ExplicitGenre? = null,

    val isVerified: Boolean = false,

    val airingTime: LocalTime? = null,

    val airingDay: DayOfWeek? = null,

    val animeRank: Int? = null,

    val duration: Int? = null,

    @Column("episodes_count")
    val episodesCount: Int? = null,

    val score: BigDecimal? = null,

    @Column("published")
    val publishedAt: LocalDate? = null,

    @Column("finished")
    val finishedAt: LocalDate? = null,


    val background: String = "",

    @Column("additional_information")
    val additionalInformation: String = "",

    @Id
    val id: Long? = null
)