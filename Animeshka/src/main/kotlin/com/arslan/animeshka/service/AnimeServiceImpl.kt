package com.arslan.animeshka.service

import com.arslan.animeshka.*
import com.arslan.animeshka.entity.*
import com.arslan.animeshka.repository.*
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaLocalTime
import kotlinx.datetime.toKotlinLocalDate
import kotlinx.datetime.toKotlinLocalTime
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.await
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class AnimeServiceImpl(
    private val animeRepository: AnimeRepository,
    private val animeSeasonsRepository: AnimeSeasonsRepository,
    private val contentRepository: ContentRepository,
    private val unverifiedContentService: UnverifiedContentService,
    private val databaseClient: DatabaseClient,
    private val contentChangeService: ContentChangeService
) : AnimeService {

    override suspend fun createUnverifiedAnime(unverifiedAnime: UnverifiedAnime) {
        unverifiedContentService.createAnimeEntry(unverifiedAnime)
    }

    override suspend fun verifyAnimeEntry(contentID: Long) {
        val animeEntry = unverifiedContentService.verifyAnime(contentID)
        val verifiedContent = contentRepository.save(Content(contentID,true))
        val season = getAnimeSeason(animeEntry)
        val anime = with(animeEntry){
            val anime = Anime(title,status,rating,studio,demographic,licensor,japaneseTitle, synopsis, animeType,season?.id,explicitGenre,airingTime?.toJavaLocalTime(),airingDay,duration,0,airedAt?.toJavaLocalDate(),finishedAt?.toJavaLocalDate(),background,additionalInfo, id = verifiedContent.id).apply { isNewEntity = true }
            animeRepository.save(anime)
        }

        createThemes(anime,animeEntry.themes)
        createGenres(anime,animeEntry.genres)
        createNovelRelations(anime,animeEntry.novelRelations)
        createAnimeRelations(anime,animeEntry.animeRelations)
        createCharacterAssociations(anime,animeEntry.characters)
    }

    override suspend fun updateAnime(anime: AnimeDTO) {
        val animeBasicData = animeRepository.findById(anime.id) ?: throw EmptyResultDataAccessException("Anime with id ${anime.id} not found.",1)
        val characters = getAnimeCharacters(animeBasicData)
        val themes = getAnimeThemes(animeBasicData)
        val genres = getAnimeGenres(animeBasicData)
        val novelRelations = getAnimeNovelRelations(animeBasicData)
        val animeRelations = getAnimeAnimeRelations(animeBasicData)

        val currentAnimeState = with(animeBasicData) {
            AnimeDTO(title,japaneseTitle,status,rating,studio,demographic,licensor,synopsis,animeType,background,additionalInformation,themes,genres,novelRelations,animeRelations,characters,explicitGenre,airingTime?.toKotlinLocalTime(),airingDay,duration,publishedAt?.toKotlinLocalDate(),finishedAt?.toKotlinLocalDate(),id)
        }

        contentChangeService.insertAnimeChanges(currentAnimeState,anime)
    }

    private suspend fun getAnimeAnimeRelations(anime: Anime) : Set<WorkRelation> =
        databaseClient
            .sql { "SELECT * FROM ANIME_ANIME_RELATIONS WHERE anime_id = :animeID" }
            .bind("animeID",anime.id)
            .map { row -> WorkRelation(row.getParam("related_anime_id"),Relation.valueOf(row.getParam("relation"))) }
            .all()
            .collectList()
            .awaitFirst()
            .toSet()

    private suspend fun getAnimeNovelRelations(anime: Anime) : Set<WorkRelation> =
        databaseClient
            .sql { "SELECT * FROM NOVEL_ANIME_RELATIONS WHERE anime_id = :animeID" }
            .bind("animeID",anime.id)
            .map { row -> WorkRelation(row.getParam("novel_id"),Relation.valueOf(row.getParam("relation"))) }
            .all()
            .collectList()
            .awaitFirst()
            .toSet()

    private suspend fun getAnimeGenres(anime: Anime) : Set<Genre> =
        databaseClient
            .sql { "SELECT * FROM ANIME_GENRES WHERE anime_id = :animeID" }
            .bind("animeID",anime.id)
            .map { row -> Genre.valueOf(row.getParam("genre")) }
            .all()
            .collectList()
            .awaitFirst()
            .toSet()


    private suspend fun getAnimeThemes(anime: Anime) : Set<Theme> =
        databaseClient
            .sql { "SELECT * FROM ANIME_THEMES WHERE anime_id = :animeID" }
            .bind("animeID",anime.id)
            .map { row -> Theme.valueOf(row.getParam("theme")) }
            .all()
            .collectList()
            .awaitFirst()
            .toSet()

    private suspend fun getAnimeCharacters(anime: Anime) : Set<AnimeCharacter> =
        databaseClient
            .sql { "SELECT * FROM ANIME_CHARACTERS WHERE anime_id = :animeID" }
            .bind("animeID",anime.id)
            .map { row -> AnimeCharacter(row.getParam("character_id"),row.getParam("voice_actor_id")) }
            .all()
            .collectList()
            .awaitFirst()
            .toSet()

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

    private suspend fun getAnimeSeason(unverifiedAnime: UnverifiedAnime): AnimeSeason? {
        return if(unverifiedAnime.airedAt == null){
            null
        }else{
            val year = unverifiedAnime.airedAt.year
            val season = Season.seasonOf(unverifiedAnime.airedAt.month)
            try{
                animeSeasonsRepository.save(AnimeSeason(season,year))
            }catch (ex: DataIntegrityViolationException){
                if(ex.message?.contains("ANIME_SEASONS_UNIQUE_SEASON_PER_YEAR") == false) throw ex
                animeSeasonsRepository.findBySeasonAndYear(season,year)
            }
        }
    }

}