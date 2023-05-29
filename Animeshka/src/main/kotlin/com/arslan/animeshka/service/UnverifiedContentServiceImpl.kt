package com.arslan.animeshka.service

import com.arslan.animeshka.UnverifiedAnime
import com.arslan.animeshka.UnverifiedStudio
import com.arslan.animeshka.entity.ContentTypeStatus
import com.arslan.animeshka.entity.NewContentType
import com.arslan.animeshka.entity.UnverifiedNewContent
import com.arslan.animeshka.repository.ANIME_PREFIX_KEY
import com.arslan.animeshka.repository.STUDIO_PREFIX_KEY
import com.arslan.animeshka.repository.UnverifiedNewContentRepository
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
class UnverifiedContentServiceImpl(
    private val json: Json,
    private val unverifiedNewContentRepository: UnverifiedNewContentRepository
) : UnverifiedContentService {

    override suspend fun createAnimeEntry(unverifiedAnime: UnverifiedAnime) {
        val content = json.encodeToString(unverifiedAnime)
        val creatorID = ReactiveSecurityContextHolder.getContext().awaitFirst().authentication.name.toLong()
        unverifiedNewContentRepository.save(UnverifiedNewContent(creatorID, NewContentType.ANIME,content, "$ANIME_PREFIX_KEY${unverifiedAnime.title}"))
    }

    override suspend fun verifyAnime(contentID: Long) : UnverifiedAnime {
        val content = verifyContent(contentID)

        return json.decodeFromString(content.content)
    }

    override suspend fun createStudioEntry(studio: UnverifiedStudio) {
        val content = json.encodeToString(studio)
        val creatorID = ReactiveSecurityContextHolder.getContext().awaitFirst().authentication.name.toLong()
        unverifiedNewContentRepository.save(UnverifiedNewContent(creatorID,NewContentType.STUDIO,content,"$STUDIO_PREFIX_KEY${studio.studioName}"))
    }

    override suspend fun verifyStudio(contentID: Long): UnverifiedStudio {
        val content = verifyContent(contentID)

        return json.decodeFromString(content.content)
    }

    private suspend fun verifyContent(contentID: Long) : UnverifiedNewContent{
        val content = unverifiedNewContentRepository.findById(contentID) ?: throw EmptyResultDataAccessException("Anime content with id $contentID not found.",1)
        if(content.contentStatus != ContentTypeStatus.UNDER_VERIFICATION) throw IllegalStateException("Unverified content is currently not under verification. Current status: ${content.contentStatus}")

        val verifier = ReactiveSecurityContextHolder.getContext().awaitFirst().authentication.name.toLong()

        if(content.verifier != verifier) throw AccessDeniedException("User with id ${content.verifier} is trying to verify content with id $contentID that is under verification by different user with id ${content.verifier}.")

        unverifiedNewContentRepository.save(content.copy(contentStatus = ContentTypeStatus.VERIFIED))

        return content
    }


}