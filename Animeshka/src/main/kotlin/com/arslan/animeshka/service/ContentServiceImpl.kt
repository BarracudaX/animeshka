package com.arslan.animeshka.service

import com.arslan.animeshka.*
import com.arslan.animeshka.entity.Content
import com.arslan.animeshka.repository.*
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class ContentServiceImpl(
    private val json: Json,
    private val contentRepository: ContentRepository
) : ContentService {

    override suspend fun createAnimeEntry(animeContent: AnimeContent) : Content{
        if(!contentRepository.existsByContentTypeAndIdAndContentStatus(ContentType.STUDIO,animeContent.studio,ContentStatus.VERIFIED)){
            throw EmptyResultDataAccessException("Studio with id ${animeContent.studio} was not found.",1)
        }

        if(!contentRepository.existsByContentTypeAndIdAndContentStatus(ContentType.STUDIO,animeContent.licensor,ContentStatus.VERIFIED)){
            throw EmptyResultDataAccessException("Licensor with id ${animeContent.studio} was not found.",1)
        }

        for((novelID,_) in animeContent.novelRelations){
            if(!contentRepository.existsByContentTypeAndIdAndContentStatus(ContentType.NOVEL,novelID,ContentStatus.VERIFIED)){
                throw EmptyResultDataAccessException("Novel with id $novelID was not found.",1)
            }
        }

        for((animeID,_) in animeContent.animeRelations){
            if(!contentRepository.existsByContentTypeAndIdAndContentStatus(ContentType.ANIME,animeID,ContentStatus.VERIFIED)){
               throw EmptyResultDataAccessException("Anime with id $animeID was not found.",1)
            }
        }

        for((characterID,personID,_) in animeContent.characters){
            if(!contentRepository.existsByContentTypeAndIdAndContentStatus(ContentType.CHARACTER,characterID,ContentStatus.VERIFIED)){
                throw EmptyResultDataAccessException("Character with id $characterID was not found.",1)
            }

            if(!contentRepository.existsByContentTypeAndIdAndContentStatus(ContentType.PERSON,personID,ContentStatus.VERIFIED)){
                throw EmptyResultDataAccessException("Voice Actor with id $personID was not found.",1)
            }
        }

        val content = json.encodeToString(animeContent)
        val creatorID = ReactiveSecurityContextHolder.getContext().awaitFirst().authentication.name.toLong()

        return contentRepository.save(Content(creatorID, ContentType.ANIME,content, "$ANIME_PREFIX_KEY${animeContent.title}"))
    }

    override suspend fun verifyAnime(contentID: Long) : AnimeContent {
        val content = verifyContent(contentID)

        return json.decodeFromString<AnimeContent>(content.content).copy(id = content.id)
    }

    override suspend fun createStudioEntry(studio: StudioContent): Content {
        val content = json.encodeToString(studio)
        val creatorID = ReactiveSecurityContextHolder.getContext().awaitFirst().authentication.name.toLong()
        return contentRepository.save(Content(creatorID, ContentType.STUDIO,content,"$STUDIO_PREFIX_KEY${studio.studioName}"))
    }

    override suspend fun verifyStudio(contentID: Long): StudioContent {
        val content = verifyContent(contentID)

        return json.decodeFromString<StudioContent>(content.content).copy(id = content.id)
    }

    override suspend fun createNovelEntry(novel: NovelContent): Content {
        for((animeID,_) in novel.animeRelations){
            if(!contentRepository.existsByContentTypeAndIdAndContentStatus(ContentType.ANIME,animeID,ContentStatus.VERIFIED)){
                throw EmptyResultDataAccessException("Anime with id $animeID was not found.",1)
            }
        }

        for((novelID,_) in novel.novelRelations){
            if(!contentRepository.existsByContentTypeAndIdAndContentStatus(ContentType.NOVEL,novelID,ContentStatus.VERIFIED)){
                throw EmptyResultDataAccessException("Novel with id $novelID was not found.",1)
            }
        }

        for((characterID,_) in novel.characters){
            if(!contentRepository.existsByContentTypeAndIdAndContentStatus(ContentType.CHARACTER,characterID,ContentStatus.VERIFIED)){
                throw EmptyResultDataAccessException("Character with id $characterID was not found.",1)
            }
        }

        val content = json.encodeToString(novel)
        val creatorID = ReactiveSecurityContextHolder.getContext().awaitFirst().authentication.name.toLong()

        return contentRepository.save(Content(creatorID, ContentType.NOVEL,content, "${NOVEL_PREFIX_KEY}${novel.title}"))
    }

    override suspend fun verifyNovel(novelID: Long): NovelContent {
        val content = verifyContent(novelID)

        return json.decodeFromString<NovelContent>(content.content).copy(id = content.id)
    }

    override suspend fun createCharacterEntry(character: CharacterContent): Content {
        val content = json.encodeToString(character)
        val creatorID = ReactiveSecurityContextHolder.getContext().awaitFirst().authentication.name.toLong()
        return contentRepository.save(Content(creatorID, ContentType.CHARACTER,content, "${CHARACTER_PREFIX_KEY}${character.characterName}"))
    }

    override suspend fun verifyCharacter(contentID: Long): CharacterContent {
        val content = verifyContent(contentID)

        return json.decodeFromString<CharacterContent>(content.content).copy(id = content.id)
    }

    override suspend fun createPersonEntry(personContent: PersonContent): Content {
        val content = json.encodeToString(personContent)
        val creatorID = ReactiveSecurityContextHolder.getContext().awaitFirst().authentication.name.toLong()
        return contentRepository.save(Content(creatorID, ContentType.PERSON,content, personContent.key))
    }

    override suspend fun verifyPerson(id: Long): PersonContent {
        val content = verifyContent(id)

        return json.decodeFromString<PersonContent>(content.content).copy(id = content.id)
    }

    override suspend fun checkContentForUpdate(id: Long): Content {

        val content = contentRepository.findById(id) ?: throw EmptyResultDataAccessException("Could not find verified content with id $id",1)

        if(content.contentStatus != ContentStatus.VERIFIED) throw IllegalStateException("Cannot add change proposals for content with id $id because of it's current state : ${content.contentStatus}.")

        return content
    }

    private suspend fun verifyContent(contentID: Long) : Content{
        val verifier = ReactiveSecurityContextHolder.getContext().awaitFirst().authentication.name.toLong()

        val content = contentRepository.findById(contentID) ?: throw EmptyResultDataAccessException("Content with id $contentID not found.",1)

        if(content.verifier != verifier) throw AccessDeniedException("User with id ${content.verifier} is trying to verify content with id $contentID that is under verification by different user with id ${content.verifier}.")

        contentRepository.save(content.copy(contentStatus = ContentStatus.VERIFIED))

        return content
    }


}