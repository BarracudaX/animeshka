package com.arslan.animeshka.entity

import org.springframework.data.relational.core.mapping.Table

class AnimeTheme(
    val animeID:Long,

    val theme: Theme
)