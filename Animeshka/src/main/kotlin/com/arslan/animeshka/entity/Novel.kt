package com.arslan.animeshka.entity

import com.arslan.animeshka.Demographic
import com.arslan.animeshka.ExplicitGenre
import com.arslan.animeshka.NovelStatus
import com.arslan.animeshka.NovelType
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate

@Table("NOVELS")
data class Novel(
    val synopsis: String,

    val published: LocalDate,

    val title: String,

    val status: NovelStatus,

    val novelType: NovelType,

    val explicitGenre: ExplicitGenre? = null,

    val magazine: Long? = null,

    val japaneseTitle: String? = null,

    val demographic: Demographic? = null,

    val novelRank: Int? = null,

    val score: Double? = null,

    val background: String = "",

    val finished: LocalDate? = null,

    val chapters: Int? = null,

    val volumes: Int? = null,

    @Id
    val id: Long
)