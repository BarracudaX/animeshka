package com.arslan.animeshka.service

import com.arslan.animeshka.CharacterContent
import com.arslan.animeshka.ContentStatus
import com.arslan.animeshka.ContentType
import com.arslan.animeshka.entity.Content
import com.arslan.animeshka.repository.CHARACTER_PREFIX_KEY
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.mono
import kotlinx.serialization.encodeToString
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DuplicateKeyException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.ReactiveSecurityContextHolder

class CharacterContentServiceITest  @Autowired constructor(private val contentService: ContentService): AbstractServiceITest() {

    private val characterContent = CharacterContent("any_name","any_japanese","any_description")

    @Test
    fun `should insert new character content`() = runTransactionalTest{
        contentRepository.findByContentKey("${CHARACTER_PREFIX_KEY}${characterContent.characterName}").shouldBeNull()
        val creatorID = createPlainUser().id!!

        val contentID = mono { contentService.createCharacterEntry(characterContent).id!! }
            .contextWrite(ReactiveSecurityContextHolder.withAuthentication(UsernamePasswordAuthenticationToken(creatorID,"")))
            .awaitSingle()

        assertSoftly(contentRepository.findById(contentID)!!) {
            content shouldEqualJson json.encodeToString(characterContent)
            contentStatus shouldBe ContentStatus.PENDING_VERIFICATION
            contentKey shouldBe "${CHARACTER_PREFIX_KEY}${characterContent.characterName}"
            contentType shouldBe ContentType.CHARACTER
            this.creatorID shouldBe creatorID
            verifier shouldBe null
            rejectionReason shouldBe null
        }
    }

    @Test
    fun `should throw DuplicateKeyException when trying to insert character content that already exists`() = runTransactionalTest{
        mono { contentService.createCharacterEntry(characterContent) }.contextWrite(createPlainUserSecContext()).awaitSingle()

        shouldThrow<DuplicateKeyException> {
            mono { contentService.createCharacterEntry(characterContent) }
                .contextWrite(createPlainUserSecContext())
                .awaitSingle()
        }
    }

    @Test
    fun `should throw EmptyResultDataAccessException when trying to verify character content that does not exist`() = runTransactionalTest{
        shouldThrow<EmptyResultDataAccessException> { mono { contentService.verifyCharacter(-1) }.contextWrite(createPlainUserSecContext()).awaitSingle() }
    }

    @Test
    fun `should throw AccessDeniedException when trying to verify character content that is not under verification`() = runTransactionalTest{
        val idOfCharacterContentThatDoesNotHaveVerifier = contentRepository.save(Content(createPlainUser().id!!,ContentType.CHARACTER,"{}","", verifier = null)).id!!

        shouldThrow<AccessDeniedException> { mono { contentService.verifyCharacter(idOfCharacterContentThatDoesNotHaveVerifier) }.contextWrite(createPlainUserSecContext()).awaitSingle() }
    }

    @Test
    fun `should throw AccessDeniedException when trying to verify character content and caller is not verifier`() = runTransactionalTest{
        val callerID = createPlainUser().id!!
        val verifierID = createPlainUser().id!!
        val contentID = contentRepository.save(Content(createPlainUser().id!!,ContentType.CHARACTER,"{}","", verifier = verifierID)).id!!
        assert(callerID != verifierID)

        shouldThrow<AccessDeniedException> {
            mono { contentService.verifyCharacter(contentID) }.contextWrite(ReactiveSecurityContextHolder.withAuthentication(UsernamePasswordAuthenticationToken(callerID,""))).awaitSingle()
        }
    }

    @Test
    fun `should verify character content successfully`() = runTransactionalTest{
        val verifierID = createPlainUser().id!!
        val contentID = contentRepository.save(Content(createPlainUser().id!!,ContentType.CHARACTER,json.encodeToString(characterContent),"", verifier = verifierID)).id!!

        mono { contentService.verifyCharacter(contentID) }.contextWrite(ReactiveSecurityContextHolder.withAuthentication(UsernamePasswordAuthenticationToken(verifierID,""))).awaitSingle()

        contentRepository.findById(contentID)!!.contentStatus shouldBe ContentStatus.VERIFIED
    }
}
