package com.arslan.animeshka.service

import com.arslan.animeshka.UnverifiedStudio
import com.arslan.animeshka.entity.Content
import com.arslan.animeshka.entity.Studio
import com.arslan.animeshka.repository.ContentRepository
import com.arslan.animeshka.repository.StudioRepository
import kotlinx.datetime.toJavaLocalDate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.annotation.Validated

@Service
@Transactional
@Validated
class StudioServiceImpl(
    private val studioRepository: StudioRepository,
    private val unverifiedContentService: UnverifiedContentService,
    private val contentRepository: ContentRepository
) : StudioService {

    override suspend fun createStudio(studio: UnverifiedStudio) {
        unverifiedContentService.createStudioEntry(studio)
    }

    override suspend fun verifyStudio(contentID: Long) {
        val verifiedStudio = unverifiedContentService.verifyStudio(contentID)
        val verifiedContent = contentRepository.save(Content(contentID,true))

        with(verifiedStudio){ studioRepository.save(Studio(studioName,japaneseName,established.toJavaLocalDate(),verifiedContent.id,true)) }

    }


}