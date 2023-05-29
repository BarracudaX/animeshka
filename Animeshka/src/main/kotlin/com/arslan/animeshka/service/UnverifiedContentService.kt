package com.arslan.animeshka.service

import com.arslan.animeshka.UnverifiedStudio
import com.arslan.animeshka.UnverifiedAnime

interface UnverifiedContentService {

    suspend fun createAnimeEntry(unverifiedAnime: UnverifiedAnime)

    suspend fun verifyAnime(contentID: Long) : UnverifiedAnime
    suspend fun createStudioEntry(studio: UnverifiedStudio)
    suspend fun verifyStudio(contentID: Long): UnverifiedStudio
}