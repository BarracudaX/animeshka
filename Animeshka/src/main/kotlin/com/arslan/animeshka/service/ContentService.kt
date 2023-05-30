package com.arslan.animeshka.service

import com.arslan.animeshka.UnverifiedStudio
import com.arslan.animeshka.UnverifiedAnime
import com.arslan.animeshka.entity.VerifiedContent

interface ContentService {

    suspend fun createAnimeEntry(unverifiedAnime: UnverifiedAnime)

    suspend fun verifyAnime(contentID: Long) : Pair<UnverifiedAnime,VerifiedContent>
    suspend fun createStudioEntry(studio: UnverifiedStudio)
    suspend fun verifyStudio(contentID: Long): Pair<UnverifiedStudio,VerifiedContent>

}