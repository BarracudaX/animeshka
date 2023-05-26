package com.arslan.animeshka.service

import com.arslan.animeshka.entity.AnimeEntry


interface AnimeService {

    suspend fun insertAnimeEntry(animeEntry: AnimeEntry)

    suspend fun verifyAnimeEntry(contentID: Long)

    suspend fun acceptModeration(contentID: Long)
}