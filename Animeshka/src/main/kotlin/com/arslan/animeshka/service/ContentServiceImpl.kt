package com.arslan.animeshka.service

import com.arslan.animeshka.*
import com.arslan.animeshka.entity.UnverifiedContent
import com.arslan.animeshka.entity.VerifiedContent
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
    private val unverifiedNewContentRepository: UnverifiedNewContentRepository,
    private val contentRepository: ContentRepository
) : ContentService {

    override suspend fun createAnimeEntry(unverifiedAnime: UnverifiedAnime) {
        val content = json.encodeToString(unverifiedAnime)
        val creatorID = ReactiveSecurityContextHolder.getContext().awaitFirst().authentication.name.toLong()
        unverifiedNewContentRepository.save(UnverifiedContent(creatorID, NewContentType.ANIME,content, "$ANIME_PREFIX_KEY${unverifiedAnime.title}"))
    }

    override suspend fun verifyAnime(contentID: Long) : Pair<UnverifiedAnime,VerifiedContent> {
        val unverifiedContent = verifyContent(contentID)
        val verifiedContent = contentRepository.save(VerifiedContent(VerifiedContentStatus.VERIFIED,unverifiedContent.id!!).apply { isNewEntity = true })

        return json.decodeFromString<UnverifiedAnime>(unverifiedContent.content) to verifiedContent
    }

    override suspend fun createStudioEntry(studio: UnverifiedStudio) {
        val content = json.encodeToString(studio)
        val creatorID = ReactiveSecurityContextHolder.getContext().awaitFirst().authentication.name.toLong()
        unverifiedNewContentRepository.save(UnverifiedContent(creatorID, NewContentType.STUDIO,content,"$STUDIO_PREFIX_KEY${studio.studioName}"))
    }

    override suspend fun verifyStudio(contentID: Long): Pair<UnverifiedStudio,VerifiedContent> {
        val unverifiedContent = verifyContent(contentID)
        val verifiedContent = contentRepository.save(VerifiedContent(VerifiedContentStatus.VERIFIED,unverifiedContent.id!!).apply { isNewEntity = true })

        return json.decodeFromString<UnverifiedStudio>(unverifiedContent.content) to verifiedContent
    }

    override suspend fun createNovelEntry(novel: UnverifiedNovel): UnverifiedContent {
        val content = json.encodeToString(novel)
        val creatorID = ReactiveSecurityContextHolder.getContext().awaitFirst().authentication.name.toLong()

        return unverifiedNewContentRepository.save(UnverifiedContent(creatorID, NewContentType.ANIME,content, "${NOVEL_PREFIX_KEY}_${novel.title}"))
    }

    override suspend fun verifyNovel(novelID: Long): Pair<UnverifiedNovel, VerifiedContent> {
        val unverifiedContent = verifyContent(novelID)
        val verifiedContent = contentRepository.save(VerifiedContent(VerifiedContentStatus.VERIFIED,unverifiedContent.id!!).apply { isNewEntity = true })

        return json.decodeFromString<UnverifiedNovel>(unverifiedContent.content) to verifiedContent
    }

    private suspend fun verifyContent(contentID: Long) : UnverifiedContent{
        val content = unverifiedNewContentRepository.findById(contentID) ?: throw EmptyResultDataAccessException("Anime content with id $contentID not found.",1)
        if(content.contentStatus != UnverifiedContentStatus.UNDER_VERIFICATION) throw IllegalStateException("Unverified content is currently not under verification. Current status: ${content.contentStatus}")

        val verifier = ReactiveSecurityContextHolder.getContext().awaitFirst().authentication.name.toLong()

        if(content.verifier != verifier) throw AccessDeniedException("User with id ${content.verifier} is trying to verify content with id $contentID that is under verification by different user with id ${content.verifier}.")

        unverifiedNewContentRepository.save(content.copy(contentStatus = UnverifiedContentStatus.VERIFIED))

        return content
    }


}