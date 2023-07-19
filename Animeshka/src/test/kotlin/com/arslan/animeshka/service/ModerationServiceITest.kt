package com.arslan.animeshka.service

import com.arslan.animeshka.ContentAlreadyUnderModerationException
import com.arslan.animeshka.ContentStatus
import com.arslan.animeshka.ContentType
import com.arslan.animeshka.entity.Content
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import org.junit.jupiter.api.Test
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import java.util.*

class ModerationServiceITest : AbstractServiceITest() {

    @Test
    fun `should throw EmptyResultDataAccessException when trying to moderate non existing content`() = runTransactionalTest{
        shouldThrow<EmptyResultDataAccessException> { moderationService.acceptModeration(1212) }
    }

    @Test
    fun `should throw ContentAlreadyUnderModerationException when trying to accept content for moderation that is already under moderation`() = runTransactionalTest{
        val creatorID = createPlainUser().id!!
        val content = contentRepository.save(Content(creatorID,ContentType.CHARACTER,"{}", UUID.randomUUID().toString()))
        val securityContext = ReactiveSecurityContextHolder.withAuthentication(UsernamePasswordAuthenticationToken(creatorID,""))
        mono{
            moderationService.acceptModeration(content.id!!)
        }.contextWrite(securityContext).awaitSingleOrNull()

        shouldThrow<ContentAlreadyUnderModerationException> { moderationService.acceptModeration(content.id!!) }

    }

    @Test
    fun `should update the status and verifier information of the content on successful verification acceptance`() = runTransactionalTest{
        val userID = createPlainUser().id!!
        val content = contentRepository.save(Content(userID,ContentType.CHARACTER,"{}", UUID.randomUUID().toString()))
        val securityContext = ReactiveSecurityContextHolder.withAuthentication(UsernamePasswordAuthenticationToken(userID,""))

        mono{
            moderationService.acceptModeration(content.id!!)

            assertSoftly(contentRepository.findById(content.id!!)!!) {
                contentStatus shouldBe ContentStatus.UNDER_VERIFICATION
                verifier shouldBe userID
            }
        }.contextWrite(securityContext).awaitSingleOrNull()
    }

    @Test
    fun `should throw EmptyResultDataAccessException when trying to reject content that doesn't exist`() = runTransactionalTest{
        shouldThrow<EmptyResultDataAccessException> { moderationService.rejectContent(1,"") }
    }

    @Test
    fun `should throw IllegalStateException when trying to reject content that is not under verification`() = runTransactionalTest {
        val userID = createPlainUser().id!!
        val content = contentRepository.save(Content(userID,ContentType.CHARACTER,"{}", UUID.randomUUID().toString()))
        val securityContext = ReactiveSecurityContextHolder.withAuthentication(UsernamePasswordAuthenticationToken(userID,""))

        mono{
            shouldThrow<IllegalStateException> { moderationService.rejectContent(content.id!!,"") }
        }.contextWrite(securityContext).awaitSingleOrNull()
    }

    @Test
    fun `should throw AccessDeniedException when user that is not responsible for verification tries to reject the content`() = runTransactionalTest{
        val creatorID = createPlainUser().id!!
        val verifierID = createPlainUser().id!!
        val otherUserID = createPlainUser().id!!
        val content = contentRepository.save(Content(creatorID,ContentType.CHARACTER,"{}", UUID.randomUUID().toString()))
        val verifierSecurityContext = ReactiveSecurityContextHolder.withAuthentication(UsernamePasswordAuthenticationToken(verifierID,""))
        val otherUserSecurityContext = ReactiveSecurityContextHolder.withAuthentication(UsernamePasswordAuthenticationToken(otherUserID,""))
        mono{ moderationService.acceptModeration(content.id!!) }.contextWrite(verifierSecurityContext).awaitSingleOrNull()

        mono {
            shouldThrow<AccessDeniedException> { moderationService.rejectContent(content.id!!,"") }
        }.contextWrite(otherUserSecurityContext).awaitSingleOrNull()
    }

    @Test
    fun `should update the content accordingly after successful rejection`() = runTransactionalTest {
        val creatorID = createPlainUser().id!!
        val verifierID = createPlainUser().id!!
        val content = contentRepository.save(Content(creatorID,ContentType.CHARACTER,"{}", UUID.randomUUID().toString()))
        val verifierSecurityContext = ReactiveSecurityContextHolder.withAuthentication(UsernamePasswordAuthenticationToken(verifierID,""))
        mono{ moderationService.acceptModeration(content.id!!) }.contextWrite(verifierSecurityContext).awaitSingleOrNull()


        mono{
            moderationService.rejectContent(content.id!!,"reason")

            assertSoftly(contentRepository.findById(content.id!!)!!) {
                contentStatus shouldBe ContentStatus.REJECTED
                rejectionReason shouldBe "reason"
            }
        }.contextWrite(verifierSecurityContext).awaitSingleOrNull()
    }
}