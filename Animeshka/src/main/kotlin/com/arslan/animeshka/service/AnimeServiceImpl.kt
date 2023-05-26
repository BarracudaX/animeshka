package com.arslan.animeshka.service

import com.arslan.animeshka.ContentAlreadyUnderModerationException
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
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class AnimeServiceImpl(
    private val animeRepository: AnimeRepository,
    private val animeSeasonsRepository: AnimeSeasonsRepository,
    private val unverifiedNewContentRepository: UnverifiedNewContentRepository,
    private val themeRepository: ThemeRepository,
    private val genreRepository: GenreRepository,
    private val relationRepository: RelationRepository,
    private val animeCharacterRepository: AnimeCharacterRepository,
    private val json: Json
) : AnimeService {

    override suspend fun insertAnimeEntry(animeEntry: AnimeEntry) {
        val content = json.encodeToString(animeEntry)
        val creatorID = ReactiveSecurityContextHolder.getContext().awaitFirst().authentication.name.toLong()
        unverifiedNewContentRepository.save(UnverifiedNewContent(creatorID,NewContentType.ANIME,content, "${ANIME_PREFIX_KEY}${animeEntry.title}"))
    }

    override suspend fun verifyAnimeEntry(contentID: Long) {
        val animeContent = unverifiedNewContentRepository.findByIdAndContentType(contentID,NewContentType.ANIME) ?: throw EmptyResultDataAccessException("Anime content with id $contentID not found.",1)
        unverifiedNewContentRepository.save(animeContent.copy(contentStatus = ContentTypeStatus.VERIFIED))
        val animeEntry = json.decodeFromString<AnimeEntry>(animeContent.content)
        val season = getAnimeSeason(animeEntry)
        val anime = with(animeEntry){
            animeRepository.save(Anime(title,status,rating,studio,demographic,licensor,japaneseTitle, synopsis, animeType,season?.id,explicitGenre,airingTime?.toJavaLocalTime(),airingDay,duration,0,airedAt?.toJavaLocalDate(),finishedAt?.toJavaLocalDate(),background,additionalInfo))
        }

        for(theme in animeEntry.themes){
            themeRepository.createAnimeThemeEntry(anime.id!!,theme)
        }

        for(genre in animeEntry.genres){
            genreRepository.createAnimeGenreEntry(anime.id!!,genre)
        }

        for((novelID,relation) in animeEntry.novelRelations){
            relationRepository.createAnimeNovelRelationEntry(anime.id!!,novelID,relation)
        }

        for((relatedAnimeID,relation) in animeEntry.animeRelations){
            relationRepository.createAnimeAnimeRelationEntry(anime.id!!,relatedAnimeID,relation)
        }

        for( (characterID,voiceActorID) in animeEntry.characters ){
            animeCharacterRepository.createAnimeCharacterEntry(anime.id!!,characterID,voiceActorID)
        }

    }

    override suspend fun acceptModeration(contentID: Long) {
        val animeContent = unverifiedNewContentRepository.findByIdAndContentType(contentID,NewContentType.ANIME) ?: throw EmptyResultDataAccessException("Anime content with id $contentID not found.",1)

        if(animeContent.verifier != null) throw ContentAlreadyUnderModerationException()

        val verifier = ReactiveSecurityContextHolder.getContext().awaitFirst().authentication.name.toLong()
        unverifiedNewContentRepository.save(animeContent.copy(verifier = verifier, contentStatus = ContentTypeStatus.UNDER_VERIFICATION))
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