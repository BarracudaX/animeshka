package com.arslan.animeshka.service

import com.arslan.animeshka.*
import com.arslan.animeshka.entity.Novel
import com.arslan.animeshka.entity.Content
import com.arslan.animeshka.repository.NovelRepository
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.http.codec.multipart.FilePart
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.await
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.nio.file.Path

@Transactional
@Service
class NovelServiceImpl(
    private val novelRepository: NovelRepository,
    private val databaseClient: DatabaseClient,
    private val messageSource: MessageSource,
    private val contentService: ContentService,
    private val imageService: ImageService
) : NovelService {


    @Value("\${image.path.location}")
    private lateinit var imageLocation: Path

    override suspend fun findByTitle(title: String) : BasicNovelDTO {
        val novel = novelRepository.findByTitleOrJapaneseTitle(title,title) ?: throw EmptyResultDataAccessException(messageSource.getMessage("novel.not.found.by.title.message",arrayOf(title),LocaleContextHolder.getLocale()),1)

        val posterURL = "/poster/${novel.posterPath.substring(novel.posterPath.lastIndexOf("/")+1)}"
        return with(novel){ BasicNovelDTO(title,japaneseTitle,synopsis,published.toKotlinLocalDate(),novelStatus,novelType,demographic,background, posterURL, finished?.toKotlinLocalDate(),id) }
    }

    override suspend fun createNovel(novel: NovelContent, poster: FilePart): Content {
        val posterPath = imageService.saveImage(poster)

        return contentService.createNovelEntry(novel.copy(posterPath = posterPath.toString()))
    }

    override suspend fun verifyNovel(novelID: Long) {
        val novelContent = contentService.verifyNovel(novelID)
        val novel = with(novelContent){
            novelRepository.save(Novel(title,japaneseTitle,synopsis,published.toJavaLocalDate(),novelStatus,novelType,demographic,posterPath,explicitGenre,magazine,null,null,background,finished?.toJavaLocalDate(),chapters,volumes,id!!).apply { isNewEntity = true })
        }

        createNovelRelations(novel,novelContent.novelRelations)
        createAnimeRelations(novel,novelContent.animeRelations)
        createCharacterRelations(novel,novelContent.characters)
        createThemes(novel,novelContent.themes)
        createGenres(novel,novelContent.genres)
    }


    private suspend fun createNovelRelations(novel: Novel,relations: Set<WorkRelation>){
        for((relatedNovelID,relation) in relations){
            databaseClient
                .sql { "INSERT INTO NOVEL_NOVEL_RELATIONS(novel_id,related_novel_id,relation) VALUES(:novelID,:relatedNovelID,:relation)" }
                .bind("novelID",novel.id)
                .bind("relatedNovelID",relatedNovelID)
                .bind("relation",relation.name)
                .await()
        }
    }

    private suspend fun createAnimeRelations(novel: Novel,relations: Set<WorkRelation>){
        for((relatedAnimeID,relation) in relations){
            databaseClient
                .sql { "INSERT INTO NOVEL_ANIME_RELATIONS(novel_id,anime_id,relation) VALUES(:novelID,:animeID,:relation)" }
                .bind("novelID",novel.id)
                .bind("animeID",relatedAnimeID)
                .bind("relation",relation.name)
                .await()
        }
    }

    private suspend fun createCharacterRelations(novel: Novel,characters: Set<Long>){
        for(characterID in characters){
            databaseClient
                .sql { "INSERT INTO NOVEL_CHARACTERS(character_id,novel_id) VALUES(:characterID,:novelID)" }
                .bind("characterID",characterID)
                .bind("novelID",novel.id)
                .await()
        }
    }

    private suspend fun createThemes(novel: Novel,themes: Set<Theme>){
        for(theme in themes){
            databaseClient
                .sql { "INSERT INTO NOVEL_THEMES(novel_id,theme) VALUES(:novelID,:theme)" }
                .bind("novelID",novel.id)
                .bind("theme",theme.name)
                .await()
        }
    }

    private suspend fun createGenres(novel: Novel,genres: Set<Genre>){
        for(genre in genres){
            databaseClient
                .sql { "INSERT INTO NOVEL_GENRES(novel_id,genre) VALUES(:novelID,:genre)" }
                .bind("novelID",novel.id)
                .bind("genre",genre.name)
                .await()
        }
    }


}