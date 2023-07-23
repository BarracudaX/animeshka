package com.arslan.animeshka.service

import com.arslan.animeshka.*
import com.arslan.animeshka.entity.Content
import com.arslan.animeshka.repository.NOVEL_PREFIX_KEY
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.mono
import kotlinx.datetime.LocalDate
import kotlinx.serialization.encodeToString
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.ReactiveSecurityContextHolder

class NovelContentServiceITest @Autowired constructor(private val contentService: ContentService): AbstractServiceITest() {

    private val novelContent = NovelContent(
        "title","japanese_title","synopsis",LocalDate.parse("2022-02-02"),NovelStatus.FINISHED,NovelType.NOVEL,Demographic.SEINEN,"background",
        themes = setOf(Theme.ART,Theme.ANTHROPOMORPHIC),genres = setOf(Genre.ADVENTURE,Genre.DRAMA), explicitGenre = ExplicitGenre.EROTICA, finished = LocalDate.parse("2023-02-02"),
        chapters = 100,volumes = 5
    )

    @Test
    fun `should insert new novel content`() = runTransactionalTest{
        contentRepository.findByContentKey("${NOVEL_PREFIX_KEY}${novelContent.title}").shouldBeNull()
        val callerID = createPlainUser().id!!
        val context = ReactiveSecurityContextHolder.withAuthentication(UsernamePasswordAuthenticationToken(callerID,""))

        mono { contentService.createNovelEntry(novelContent) }.contextWrite(context).awaitSingle()

        assertSoftly(contentRepository.findByContentKey("${NOVEL_PREFIX_KEY}${novelContent.title}")!!) {
            id shouldNotBe null
            verifier shouldBe null
            rejectionReason shouldBe null
            contentType shouldBe ContentType.NOVEL
            contentStatus shouldBe ContentStatus.PENDING_VERIFICATION
            creatorID shouldBe callerID
            contentKey shouldBe "${NOVEL_PREFIX_KEY}${novelContent.title}"
            content shouldEqualJson json.encodeToString(novelContent)
        }
    }

    @Test
    fun `should throw EmptyResultDataAccessException when trying to insert novel with anime relation for anime that does not exist`() = runTransactionalTest{
        shouldThrow<EmptyResultDataAccessException> {
            mono { contentService.createNovelEntry(novelContent.copy(animeRelations = setOf(WorkRelation(-1,Relation.SEQUEL)))) }
                .contextWrite(createPlainUserSecContext())
                .awaitSingle()
        }
    }

    @Test
    fun `should throw EmptyResultDataAccessException when trying to insert novel with novel relation for novel that does not exist`() = runTransactionalTest{
        shouldThrow<EmptyResultDataAccessException> {
            mono { contentService.createNovelEntry(novelContent.copy(novelRelations = setOf(WorkRelation(-1,Relation.SEQUEL)))) }
                .contextWrite(createPlainUserSecContext())
                .awaitSingle()
        }
    }

    @Test
    fun `should throw EmptyResultDataAccessException when trying to insert novel character for character that does not exist`() = runTransactionalTest {
        shouldThrow<EmptyResultDataAccessException> {
            mono { contentService.createNovelEntry(novelContent.copy(characters = setOf(NovelCharacter(-1,CharacterRole.PROTAGONIST)))) }.contextWrite(createPlainUserSecContext()).awaitSingle()
        }
    }

    @Test
    fun `should throw EmptyResultDataAccessException when trying to verify novel content that does not exist`() = runTransactionalTest{
        shouldThrow<EmptyResultDataAccessException> { mono { contentService.verifyNovel(-1) }.contextWrite(createPlainUserSecContext()).awaitSingle() }
    }

    @Test
    fun `should throw AccessDeniedException when trying to verify novel that is not under verification`() = runTransactionalTest{
        val idOfContentThatDoesNotHaveVerifier = contentRepository.save(Content(createPlainUser().id!!,ContentType.NOVEL,"{}","")).id!!

        shouldThrow<AccessDeniedException> { mono { contentService.verifyNovel(idOfContentThatDoesNotHaveVerifier) }.contextWrite(createPlainUserSecContext()).awaitSingle() }
    }

    @Test
    fun `should throw AccessDeniedException when trying to verify novel and the caller is not the verifier`() = runTransactionalTest{
        val callerID = createPlainUser().id!!
        val verifierID = createPlainUser().id!!
        assert(callerID != verifierID)
        val contentID = contentRepository.save(Content(createPlainUser().id!!,ContentType.NOVEL,"{}","",ContentStatus.UNDER_VERIFICATION, verifier = verifierID)).id!!

        shouldThrow<AccessDeniedException> {
            mono { contentService.verifyNovel(contentID) }
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(UsernamePasswordAuthenticationToken(callerID,"")))
                .awaitSingle()
        }
    }

    @Test
    fun `should verify content successfully`() = runTransactionalTest{
        val verifierID = createPlainUser().id!!
        val contentID = contentRepository.save(Content(createPlainUser().id!!,ContentType.NOVEL,json.encodeToString(novelContent),"",ContentStatus.UNDER_VERIFICATION, verifier = verifierID)).id!!

        mono { contentService.verifyNovel(contentID) }.contextWrite(ReactiveSecurityContextHolder.withAuthentication(UsernamePasswordAuthenticationToken(verifierID,""))).awaitSingle()

        contentRepository.findById(contentID)!!.contentStatus shouldBe ContentStatus.VERIFIED
    }

}