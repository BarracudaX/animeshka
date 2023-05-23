package com.arslan.animeshka.service

import com.arslan.animeshka.entity.*
import com.arslan.animeshka.repository.AnimeRepository
import com.arslan.animeshka.repository.AnimeSeasonsRepository
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaLocalTime
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.r2dbc.core.insert
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class AnimeServiceImpl(private val animeRepository: AnimeRepository,private val animeSeasonsRepository: AnimeSeasonsRepository,private val r2dbcEntityTemplate: R2dbcEntityTemplate) : AnimeService {

    override suspend fun insertAnimeEntry(animeEntry: AnimeEntry) {
        val season = getAnimeSeason(animeEntry)

        val anime = with(animeEntry){
            animeRepository.save(
                Anime(
                    title,
                    status,
                    rating,
                    studio,
                    demographic,
                    licensor,
                    japaneseTitle,
                    synopsis,
                    animeType,
                    season?.id,
                    explicitGenre,
                    false,
                    airingTime?.toJavaLocalTime(),
                    airingDay,null,
                    duration,
                    episodeCounts,
                    null,
                    airedAt?.toJavaLocalDate(),
                    finishedAt?.toJavaLocalDate()
                )
            )
        }

        animeEntry.themes.forEach { theme ->
            r2dbcEntityTemplate.insert<AnimeTheme>().using(AnimeTheme(anime.id!!,theme)).awaitFirst()
        }

    }

    private suspend fun getAnimeSeason(animeEntry: AnimeEntry): AnimeSeason? {
        return if(animeEntry.airedAt == null){
            null
        }else{
            val year = animeEntry.airedAt.year
            val season = Season.seasonOf(animeEntry.airedAt.month)
            try{
                animeSeasonsRepository.save(AnimeSeason(season,year))
            }catch (ex: DataIntegrityViolationException){
                if(ex.message?.contains("ANIME_SEASONS_UNIQUE_SEASON_PER_YEAR") == false) throw ex
                animeSeasonsRepository.findBySeasonAndYear(season,year)
            }
        }
    }

}