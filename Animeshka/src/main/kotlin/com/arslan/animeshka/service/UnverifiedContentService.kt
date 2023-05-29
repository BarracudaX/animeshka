package com.arslan.animeshka.service

import com.arslan.animeshka.AnimeEntry

interface UnverifiedContentService {

    suspend fun createAnimeEntry(animeEntry: AnimeEntry)

    suspend fun verifyAnimeContent(contentID: Long) : AnimeEntry
}