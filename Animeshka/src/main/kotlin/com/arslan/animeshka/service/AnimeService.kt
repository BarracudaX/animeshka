package com.arslan.animeshka.service

import com.arslan.animeshka.AnimeEntry


interface AnimeService {

    suspend fun insertAnimeEntry(animeEntry: AnimeEntry)

    suspend fun verifyAnimeEntry(contentID: Long)

}