package com.arslan.animeshka.service

import com.arslan.animeshka.AnimeDTO
import com.arslan.animeshka.AnimeContent
import com.arslan.animeshka.PagedBasicAnimeDTO
import com.arslan.animeshka.entity.Anime
import org.springframework.data.domain.Pageable


interface AnimeService {

    suspend fun insertAnime(animeContent: AnimeContent) : Anime

    suspend fun updateAnime(anime: AnimeDTO)
    suspend fun search(searchKey: String, pageable: Pageable): PagedBasicAnimeDTO

}