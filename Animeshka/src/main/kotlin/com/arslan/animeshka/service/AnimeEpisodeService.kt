package com.arslan.animeshka.service

import com.arslan.animeshka.AnimeEpisodeEntry

interface AnimeEpisodeService {

    suspend fun createAnimeEpisodeEntry(animeEpisodeEntry: AnimeEpisodeEntry)
}