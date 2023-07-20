package com.arslan.animeshka.service

import com.arslan.animeshka.ContentStatus
import com.arslan.animeshka.ContentType
import com.arslan.animeshka.StudioContent
import com.arslan.animeshka.entity.Content
import com.arslan.animeshka.repository.STUDIO_PREFIX_KEY
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.mono
import kotlinx.datetime.LocalDate
import kotlinx.serialization.encodeToString
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DuplicateKeyException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.ReactiveSecurityContextHolder

class StudioContentServiceITest @Autowired constructor(private val contentService: ContentService) : AbstractServiceITest() {

    private val studioContent = StudioContent("test","test_jp", LocalDate.parse("2022-02-02"))

    @Test
    fun `should create studio entry`() = runTransactionalTest{
        val creatorID = createPlainUser().id!!
        contentRepository.findByContentKey("${STUDIO_PREFIX_KEY}${studioContent.studioName}").shouldBeNull()

        val contentID = mono { contentService.createStudioEntry(studioContent) }
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(UsernamePasswordAuthenticationToken(creatorID,"")))
                .awaitSingle().id!!

        assertSoftly(contentRepository.findByContentKey("${STUDIO_PREFIX_KEY}${studioContent.studioName}")!!) {
            id shouldBe contentID
            contentStatus shouldBe ContentStatus.PENDING_VERIFICATION
            rejectionReason shouldBe null
            verifier shouldBe null
            contentKey shouldBe "${STUDIO_PREFIX_KEY}${studioContent.studioName}"
            contentType shouldBe ContentType.STUDIO
            this.creatorID shouldBe creatorID
            content shouldEqualJson json.encodeToString(studioContent)
        }
    }

    @Test
    fun `should throw DuplicateKeyException when trying to insert studio entry with studio name that already exists`() = runTransactionalTest{
        val creatorID = createPlainUser().id!!
        mono { contentService.createStudioEntry(studioContent) }
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(UsernamePasswordAuthenticationToken(creatorID,"")))
                .awaitSingle()

        shouldThrow<DuplicateKeyException> {
            mono { contentService.createStudioEntry(studioContent) }
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(UsernamePasswordAuthenticationToken(creatorID,"")))
                    .awaitSingle()
        }
    }

    @Test
    fun `should throw EmptyResultDataAccessException when trying to verify studio that does not exist`() = runTransactionalTest{
        val creatorID = createPlainUser().id!!

        shouldThrow<EmptyResultDataAccessException> {
            mono { contentService.verifyStudio(-1) }.contextWrite(ReactiveSecurityContextHolder.withAuthentication(UsernamePasswordAuthenticationToken(creatorID,"")))
                    .awaitSingle()
        }
    }

    @Test
    fun `should throw AccessDeniedException when trying to verify studio that is not under verification`()  = runTransactionalTest{
        val creatorID = createPlainUser().id!!
        val contentID = mono { contentService.createStudioEntry(studioContent) }
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(UsernamePasswordAuthenticationToken(creatorID,"")))
                .awaitSingle().id!!

        shouldThrow<AccessDeniedException> {
            mono { contentService.verifyStudio(contentID) }
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(UsernamePasswordAuthenticationToken(creatorID,"")))
                    .awaitSingle()
        }
    }

    @Test
    fun `should throw AccessDeniedException when trying to verify studio that is under verification of another user`() = runTransactionalTest{
        val creatorID = createPlainUser().id!!
        val verifierID = createPlainUser().id!!
        val callerID = createPlainUser().id!!
        val contentID = mono { contentService.createStudioEntry(studioContent) }
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(UsernamePasswordAuthenticationToken(creatorID,"")))
                .awaitSingle().id!!
        contentRepository.save(Content(creatorID,ContentType.STUDIO,"{}","", verifier = verifierID,id = contentID))

        shouldThrow<AccessDeniedException> {
            mono { contentService.verifyStudio(contentID) }
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(UsernamePasswordAuthenticationToken(callerID,"")))
                    .awaitSingle()
        }
    }
}