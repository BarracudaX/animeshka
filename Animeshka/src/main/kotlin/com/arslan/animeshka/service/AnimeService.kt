package com.arslan.animeshka.service

import com.arslan.animeshka.AnimeDTO
import com.arslan.animeshka.BasicAnimeDTO
import com.arslan.animeshka.AnimeContent
import com.arslan.animeshka.entity.Content
import org.springframework.http.codec.multipart.FilePart


interface AnimeService {

    suspend fun createUnverifiedAnime(animeContent: AnimeContent, poster: FilePart) : Content

    suspend fun verifyAnimeEntry(contentID: Long)

    suspend fun updateAnime(anime: AnimeDTO)
    suspend fun findAnimeByTitle(title: String): BasicAnimeDTO

}