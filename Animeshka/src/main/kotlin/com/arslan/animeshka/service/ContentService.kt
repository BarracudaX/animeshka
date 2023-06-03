package com.arslan.animeshka.service

import com.arslan.animeshka.StudioContent
import com.arslan.animeshka.AnimeContent
import com.arslan.animeshka.CharacterContent
import com.arslan.animeshka.NovelContent
import com.arslan.animeshka.entity.Content

interface ContentService {

    suspend fun createAnimeEntry(animeContent: AnimeContent) : Content

    suspend fun verifyAnime(contentID: Long) : AnimeContent
    suspend fun createStudioEntry(studio: StudioContent) : Content
    suspend fun verifyStudio(contentID: Long): StudioContent
    suspend fun createNovelEntry(novel: NovelContent) : Content
    suspend fun verifyNovel(novelID: Long): NovelContent
    suspend fun createCharacterEntry(character: CharacterContent): Content
    suspend fun verifyCharacter(contentID: Long) : CharacterContent

}