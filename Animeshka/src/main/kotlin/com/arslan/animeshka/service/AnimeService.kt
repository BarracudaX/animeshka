package com.arslan.animeshka.service

import com.arslan.animeshka.AnimeDTO
import com.arslan.animeshka.BasicAnimeDTO
import com.arslan.animeshka.AnimeContent
import com.arslan.animeshka.entity.Anime
import com.arslan.animeshka.entity.Content
import kotlinx.coroutines.flow.Flow
import org.springframework.http.codec.multipart.FilePart


interface AnimeService {

    suspend fun createUnverifiedAnime(animeContent: AnimeContent, poster: FilePart) : Content

    suspend fun verifyAnimeEntry(contentID: Long) : Anime

    suspend fun updateAnime(anime: AnimeDTO)
    suspend fun findAnimeByTitle(title: String): Flow<BasicAnimeDTO>

}