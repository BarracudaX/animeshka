package com.arslan.animeshka.service

import com.arslan.animeshka.entity.Studio
import com.arslan.animeshka.StudioEntry
import com.arslan.animeshka.entity.NewContentType
import com.arslan.animeshka.entity.UnverifiedNewContent
import com.arslan.animeshka.repository.STUDIO_PREFIX_KEY
import com.arslan.animeshka.repository.StudioRepository
import com.arslan.animeshka.repository.UnverifiedNewContentRepository
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.datetime.toJavaLocalDate
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.annotation.Validated

@Service
@Transactional
@Validated
class StudioServiceImpl(
    private val studioRepository: StudioRepository,
    private val contentRepository: UnverifiedNewContentRepository,
    private val json: Json
) : StudioService {

    override suspend fun createStudio(studio: StudioEntry) {
        val content = json.encodeToString(studio)
        val creatorID = ReactiveSecurityContextHolder.getContext().awaitFirst().authentication.name.toLong()
        contentRepository.save(UnverifiedNewContent(creatorID,NewContentType.STUDIO,content,"${STUDIO_PREFIX_KEY}${studio.studioName}"))
    }

}