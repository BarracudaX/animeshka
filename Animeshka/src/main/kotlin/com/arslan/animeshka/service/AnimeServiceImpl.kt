package com.arslan.animeshka.service

import com.arslan.animeshka.AnimeCharacter
import com.arslan.animeshka.AnimeEntry
import com.arslan.animeshka.WorkRelation
import com.arslan.animeshka.entity.*
import com.arslan.animeshka.repository.*
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaLocalTime
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.await
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class AnimeServiceImpl(
    private val animeRepository: AnimeRepository,
    private val animeSeasonsRepository: AnimeSeasonsRepository,
    private val contentRepository: ContentRepository,
    private val unverifiedContentService: UnverifiedContentService,
    private val databaseClient: DatabaseClient
) : AnimeService {

    override suspend fun insertAnimeEntry(animeEntry: AnimeEntry) {
        unverifiedContentService.createAnimeEntry(animeEntry)
    }

    override suspend fun verifyAnimeEntry(contentID: Long) {
        val animeEntry = unverifiedContentService.verifyAnimeContent(contentID)
        val content = contentRepository.save(Content())
        val season = getAnimeSeason(animeEntry)
        val anime = with(animeEntry){
            animeRepository.save(Anime(title,status,rating,studio,demographic,licensor,japaneseTitle, synopsis, animeType,season?.id,explicitGenre,airingTime?.toJavaLocalTime(),airingDay,duration,0,airedAt?.toJavaLocalDate(),finishedAt?.toJavaLocalDate(),background,additionalInfo, id = content.id!!))
        }

        createThemes(anime,animeEntry.themes)
        createGenres(anime,animeEntry.genres)
        createNovelRelations(anime,animeEntry.novelRelations)
        createAnimeRelations(anime,animeEntry.animeRelations)
        createCharacterAssociations(anime,animeEntry.characters)
    }

    private suspend fun createCharacterAssociations(anime: Anime,characters: Set<AnimeCharacter>){
        for( (characterID,voiceActorID) in characters ){
            databaseClient
                .sql { "INSERT INTO ANIME_CHARACTERS(character_id,anime_id,voice_actor_id) VALUES(:characterID,:animeID,:voiceActorID)" }
                .bind("characterID",characterID)
                .bind("animeID",anime.id)
                .bind("voiceActorID",voiceActorID)
                .await()
        }
    }

    private suspend fun createAnimeRelations(anime: Anime,relations: Set<WorkRelation>){
        for((relatedAnimeID,relation) in relations){
            databaseClient
                .sql { "INSERT INTO ANIME_ANIME_RELATIONS(anime_id,related_anime_id,relation) VALUES(:animeID,:relatedAnimeID,:relation)" }
                .bind("animeID",anime.id)
                .bind("relatedAnimeID",relatedAnimeID)
                .bind("relation",relation.name)
                .await()
        }
    }

    private suspend fun createNovelRelations(anime: Anime, relations: Set<WorkRelation>) {
        for((novelID,relation) in relations){
            databaseClient
                .sql { "INSERT INTO NOVEL_ANIME_RELATIONS(novel_id,anime_id,relation) VALUES(:novelID,:animeID,:relation)" }
                .bind("novelID",novelID)
                .bind("animeID",anime.id)
                .bind("relation",relation.name)
                .await()
        }
    }

    private suspend fun createThemes(anime: Anime, themes: Set<Theme>) {
        for(theme in themes){
            databaseClient
                .sql { "INSERT INTO ANIME_THEMES(anime_id,theme) VALUES(:animeID,:theme)" }
                .bind("animeID",anime.id)
                .bind("theme",theme.name)
                .await()
        }
    }

    private suspend fun createGenres(anime: Anime, genres: Set<Genre>){
        for(genre in genres){
            databaseClient
                .sql { "INSERT INTO ANIME_GENRES(anime_id,genre) VALUES(:animeID,:genre)" }
                .bind("animeID",anime.id)
                .bind("genre",genre.name)
                .await()
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