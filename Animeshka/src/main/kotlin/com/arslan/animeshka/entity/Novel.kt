package com.arslan.animeshka.entity

import com.arslan.animeshka.Demographic
import com.arslan.animeshka.ExplicitGenre
import com.arslan.animeshka.NovelStatus
import com.arslan.animeshka.NovelType
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate

@Table("NOVELS")
data class Novel(
    val title: String,

    val japaneseTitle: String,

    val synopsis: String,

    val published: LocalDate,

    @Column("novel_status")
    val novelStatus: NovelStatus,

    val novelType: NovelType,

    val demographic: Demographic,

    @Column("poster_path")
    val posterPath: String,

    val explicitGenre: ExplicitGenre? = null,

    val magazine: Long? = null,

    val novelRank: Int? = null,

    val score: Double? = null,

    val background: String = "",

    val finished: LocalDate? = null,

    val chapters: Int? = null,

    val volumes: Int? = null,

    @Id
    val id: Long
) : Persistable<Long> {

    @Transient
    var isNewEntity: Boolean = false

    override fun getId(): Long = id

    override fun isNew(): Boolean = isNewEntity

}