package com.arslan.animeshka.service

import com.arslan.animeshka.AnimeDTO
import com.arslan.animeshka.UnverifiedAnime


interface AnimeService {

    suspend fun createUnverifiedAnime(unverifiedAnime: UnverifiedAnime)

    suspend fun verifyAnimeEntry(contentID: Long)

    suspend fun updateAnime(anime: AnimeDTO)
}