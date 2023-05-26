package com.arslan.animeshka.repository

import com.arslan.animeshka.entity.Genre

interface GenreRepository {

    suspend fun createAnimeGenreEntry(animeID: Long, genre: Genre)

}