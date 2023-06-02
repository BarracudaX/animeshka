package com.arslan.animeshka.service

import com.arslan.animeshka.*
import com.arslan.animeshka.entity.*
import com.arslan.animeshka.repository.*
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaLocalTime
import kotlinx.datetime.toKotlinLocalDate
import kotlinx.datetime.toKotlinLocalTime
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.http.codec.multipart.FilePart
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.await
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.nio.file.Path
import kotlin.io.path.exists

@Transactional
@Service
class AnimeServiceImpl(
    private val animeRepository: AnimeRepository,
    private val animeSeasonsRepository: AnimeSeasonsRepository,
    private val contentService: ContentService,
    private val contentRepository: ContentRepository,
    private val databaseClient: DatabaseClient,
    private val contentChangeService: ContentChangeService,
    private val imageRepository: ImageRepository,
    private val messageSource: MessageSource
) : AnimeService {

    @Value("\${image.path.location}")
    private lateinit var imageLocation: Path


    override suspend fun createUnverifiedAnime(unverifiedAnime: UnverifiedAnime,image: FilePart): UnverifiedContent {
        val imagePath = getImagePath(imageLocation,image,imageRepository.save(Image()).id!!)

        if(imagePath.exists()) throw IllegalStateException("File for anime already exists. File path $imagePath.")

        image.transferTo(imagePath).awaitFirstOrNull()

        return contentService.createAnimeEntry(unverifiedAnime.copy(imagePath = imagePath.toString()))
    }

    override suspend fun verifyAnimeEntry(contentID: Long) {
        val (animeEntry,verifiedContent) = contentService.verifyAnime(contentID)
        val season = getAnimeSeason(animeEntry)
        val anime = with(animeEntry){
            val anime = Anime(title,status,rating,studio,demographic,licensor,japaneseTitle, synopsis, imagePath,animeType,season?.id,explicitGenre,airingTime?.toJavaLocalTime(),airingDay,duration,0,airedAt?.toJavaLocalDate(),finishedAt?.toJavaLocalDate(),background,additionalInfo, id = verifiedContent.id).apply { isNewEntity = true }
            animeRepository.save(anime)
        }

        createThemes(anime,animeEntry.themes)
        createGenres(anime,animeEntry.genres)
        createNovelRelations(anime,animeEntry.novelRelations)
        createAnimeRelations(anime,animeEntry.animeRelations)
        createCharacterAssociations(anime,animeEntry.characters)
    }

    override suspend fun updateAnime(anime: AnimeDTO) {
        val content = contentRepository.findById(anime.id) ?: throw EmptyResultDataAccessException("Could not find verified anime content with id ${anime.id}",1)

        if(content.contentStatus != VerifiedContentStatus.VERIFIED) throw IllegalStateException("Cannot add change proposals for anime with id ${anime.id} because of it's current state : ${content.contentStatus}.")

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

    override suspend fun findAnimeByTitle(title: String): BasicAnimeDTO {
        val anime = animeRepository.findByTitleOrJapaneseTitle(title,title) ?: throw EmptyResultDataAccessException(messageSource.getMessage("anime.not.found.by.title.message",arrayOf(title),LocaleContextHolder.getLocale()),1)

        val posterPath = "/poster/${anime.posterPath.substring(anime.posterPath.lastIndexOf("\\")+1)}"

        return with(anime){ BasicAnimeDTO(title,japaneseTitle,status,demographic,synopsis,animeType,posterPath,id,background,publishedAt?.toKotlinLocalDate(),finishedAt?.toKotlinLocalDate()) }
    }


    private suspend fun getAnimeAnimeRelations(anime: Anime) : Set<WorkRelation> =
        databaseClient
            .sql { "SELECT * FROM ANIME_ANIME_RELATIONS WHERE anime_id = :animeID" }
            .bind("animeID",anime.id)
            .map { row -> WorkRelation(row.getParam("related_anime_id"), Relation.valueOf(row.getParam("relation"))) }
            .all()
            .collectList()
            .awaitFirst()
            .toSet()

    private suspend fun getAnimeNovelRelations(anime: Anime) : Set<WorkRelation> =
        databaseClient
            .sql { "SELECT * FROM NOVEL_ANIME_RELATIONS WHERE anime_id = :animeID" }
            .bind("animeID",anime.id)
            .map { row -> WorkRelation(row.getParam("novel_id"), Relation.valueOf(row.getParam("relation"))) }
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