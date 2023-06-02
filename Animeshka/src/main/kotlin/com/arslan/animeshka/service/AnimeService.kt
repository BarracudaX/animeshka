package com.arslan.animeshka.service

import com.arslan.animeshka.AnimeDTO
import com.arslan.animeshka.BasicAnimeDTO
import com.arslan.animeshka.UnverifiedAnime
import com.arslan.animeshka.entity.UnverifiedContent
import org.springframework.http.codec.multipart.FilePart


interface AnimeService {

    suspend fun createUnverifiedAnime(unverifiedAnime: UnverifiedAnime,image: FilePart) : UnverifiedContent

    suspend fun verifyAnimeEntry(contentID: Long)

    suspend fun updateAnime(anime: AnimeDTO)
    suspend fun findAnimeByTitle(title: String): BasicAnimeDTO

}