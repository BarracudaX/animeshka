package com.arslan.animeshka.entity

import org.springframework.data.relational.core.mapping.Table

class AnimeGenre(
    val animeID: Long,

    val genre: Genre
)