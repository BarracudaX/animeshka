package com.arslan.animeshka.service

import com.arslan.animeshka.entity.AnimeEntry


interface AnimeService {

    suspend fun insertAnimeEntry(animeEntry: AnimeEntry)

}