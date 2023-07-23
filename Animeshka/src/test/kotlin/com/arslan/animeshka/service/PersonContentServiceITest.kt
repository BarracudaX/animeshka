package com.arslan.animeshka.service

import com.arslan.animeshka.ContentStatus
import com.arslan.animeshka.ContentType
import com.arslan.animeshka.PersonContent
import com.arslan.animeshka.entity.Content
import com.arslan.animeshka.key
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.assertions.throwables.shouldThrow
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

class PersonContentServiceITest @Autowired constructor(private val contentService: ContentService): AbstractServiceITest() {

    private val personContent = PersonContent("any_fn","any_ln","any_fn","any_gn","any_desc",LocalDate.parse("1998-02-28"))

    @Test
    fun `should insert new person content`() = runTransactionalTest{
        contentRepository.findByContentKey(personContent.key) shouldBe null
        val callerID = createPlainUser().id!!

        val id = mono{contentService.createPersonEntry(personContent).id!!}
            .contextWrite(ReactiveSecurityContextHolder.withAuthentication(UsernamePasswordAuthenticationToken(callerID,"")))
            .awaitSingle()

        assertSoftly(contentRepository.findById(id)!!) {
            contentStatus shouldBe ContentStatus.PENDING_VERIFICATION
            content shouldEqualJson json.encodeToString(personContent)
            contentType shouldBe ContentType.PERSON
            verifier shouldBe null
            rejectionReason shouldBe null
            contentKey shouldBe personContent.key
            creatorID shouldBe callerID
        }
    }

    @Test
    fun `should throw DuplicateKeyException when trying to insert person content that already exists`() = runTransactionalTest{
        mono { contentService.createPersonEntry(personContent) }.contextWrite(createPlainUserSecContext()).awaitSingle()

        shouldThrow<DuplicateKeyException> {
            mono { contentService.createPersonEntry(personContent) }.contextWrite(createPlainUserSecContext()).awaitSingle()
        }
    }

    @Test
    fun `should throw EmptyResultDataAccessException when trying to verify person content that does not exist`() = runTransactionalTest{
        shouldThrow<EmptyResultDataAccessException> { mono { contentService.verifyPerson(-1) }.contextWrite(createPlainUserSecContext()).awaitSingle() }
    }

    @Test
    fun `should throw AccessDeniedException when trying to verify person content that is not under verification`() = runTransactionalTest{
        val idOfPersonContentThatDoesNotHaveVerifier = contentRepository.save(Content(createPlainUser().id!!,ContentType.PERSON,"{}","", verifier = null)).id!!

        shouldThrow<AccessDeniedException> {
            mono { contentService.verifyPerson(idOfPersonContentThatDoesNotHaveVerifier) }
                .contextWrite(createPlainUserSecContext())
                .awaitSingle()
        }
    }

    @Test
    fun `should throw AccessDeniedException when trying to verify person content and the caller is not the verifier`() = runTransactionalTest{
        val verifierID = createPlainUser().id!!
        val callerID = createPlainUser().id!!
        assert(verifierID != callerID)
        val contentID = contentRepository.save(Content(createPlainUser().id!!,ContentType.PERSON,"{}","", verifier = verifierID)).id!!

        shouldThrow<AccessDeniedException> {
            mono { contentService.verifyPerson(contentID) }
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(UsernamePasswordAuthenticationToken(callerID,"")))
                .awaitSingle()
        }
    }

    @Test
    fun `should verify person content successfully`() = runTransactionalTest{
        val verifierID = createPlainUser().id!!
        val contentID = contentRepository.save(Content(createPlainUser().id!!,ContentType.PERSON,json.encodeToString(personContent),"", verifier = verifierID)).id!!

        mono { contentService.verifyPerson(contentID) }
            .contextWrite(ReactiveSecurityContextHolder.withAuthentication(UsernamePasswordAuthenticationToken(verifierID,"")))
            .awaitSingle()

        contentRepository.findById(contentID)!!.contentStatus shouldBe ContentStatus.VERIFIED
    }
}