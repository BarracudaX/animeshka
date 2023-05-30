package com.arslan.animeshka.service

import com.arslan.animeshka.ContentAlreadyUnderModerationException
import com.arslan.animeshka.UnverifiedContentStatus
import com.arslan.animeshka.repository.UnverifiedNewContentRepository
import kotlinx.coroutines.reactive.awaitFirst
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class ModerationServiceImpl(private val contentRepository: UnverifiedNewContentRepository) : ModerationService {
    override suspend fun acceptModeration(contentID: Long) {
        val content = contentRepository.findById(contentID) ?: throw EmptyResultDataAccessException("Anime content with id $contentID not found.",1)

        if(content.verifier != null) throw ContentAlreadyUnderModerationException()

        val verifier = ReactiveSecurityContextHolder.getContext().awaitFirst().authentication.name.toLong()
        contentRepository.save(content.copy(verifier = verifier, contentStatus = UnverifiedContentStatus.UNDER_VERIFICATION))
    }

    override suspend fun rejectContent(contentID: Long, rejectionReason: String) {
        val content = contentRepository.findById(contentID) ?: throw EmptyResultDataAccessException("Anime content with id $contentID not found.",1)
        val verifier = ReactiveSecurityContextHolder.getContext().awaitFirst().authentication.name.toLong()

        if(content.contentStatus != UnverifiedContentStatus.UNDER_VERIFICATION) throw IllegalStateException("Content cannot be rejected because it is not under moderation. Current content status : ${content.contentStatus}.")

        if(content.verifier != verifier) throw AccessDeniedException("User with id $verifier is trying to modify content with id $contentID that is under moderation by user with id ${content.verifier}")

        contentRepository.save(content.copy(rejectionReason = rejectionReason, contentStatus = UnverifiedContentStatus.REJECTED))
    }

}