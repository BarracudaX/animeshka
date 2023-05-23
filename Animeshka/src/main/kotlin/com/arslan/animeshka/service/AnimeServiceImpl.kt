package com.arslan.animeshka.service

import com.arslan.animeshka.entity.Anime
import com.arslan.animeshka.entity.AnimeEntry
import com.arslan.animeshka.entity.AnimeTheme
import com.arslan.animeshka.repository.AnimeRepository
import com.arslan.animeshka.repository.AnimeSeasonsRepository
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaLocalTime
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.r2dbc.core.insert
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class AnimeServiceImpl(private val animeRepository: AnimeRepository,private val animeSeasonsRepository: AnimeSeasonsRepository,private val r2dbcEntityTemplate: R2dbcEntityTemplate) : AnimeService {

    override suspend fun insertAnimeEntry(animeEntry: AnimeEntry) {

        val anime = with(animeEntry){
            animeRepository.save(Anime(title,status,rating,studio,demographic,licensor,seasonID,japaneseTitle,animeType,explicitGenre,false,airingTime?.toJavaLocalTime(), airingDay,null,duration,episodeCounts,null,airedAt?.toJavaLocalDate(),finishedAt?.toJavaLocalDate()))
        }

        animeEntry.themes.forEach { theme ->
            r2dbcEntityTemplate.insert<AnimeTheme>().using(AnimeTheme(anime.id!!,theme)).awaitFirst()
        }

    }

}