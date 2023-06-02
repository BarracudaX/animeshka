package com.arslan.animeshka.service

import com.arslan.animeshka.UnverifiedStudio
import com.arslan.animeshka.UnverifiedAnime
import com.arslan.animeshka.UnverifiedNovel
import com.arslan.animeshka.entity.UnverifiedContent
import com.arslan.animeshka.entity.VerifiedContent

interface ContentService {

    suspend fun createAnimeEntry(unverifiedAnime: UnverifiedAnime) : UnverifiedContent

    suspend fun verifyAnime(contentID: Long) : Pair<UnverifiedAnime,VerifiedContent>
    suspend fun createStudioEntry(studio: UnverifiedStudio) : UnverifiedContent
    suspend fun verifyStudio(contentID: Long): Pair<UnverifiedStudio,VerifiedContent>
    suspend fun createNovelEntry(novel: UnverifiedNovel) : UnverifiedContent
    suspend fun verifyNovel(novelID: Long): Pair<UnverifiedNovel, VerifiedContent>

}