package com.arslan.animeshka.service

import com.arslan.animeshka.*
import com.arslan.animeshka.entity.Content
import com.arslan.animeshka.repository.ANIME_PREFIX_KEY
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.mono
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.serialization.encodeToString
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DuplicateKeyException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import java.time.DayOfWeek

class AnimeContentServiceITest  @Autowired constructor(private val contentService: ContentService): AbstractServiceITest() {

    private val animeData = AnimeContent(
            "test","jp",AnimeStatus.AIRING,SeriesRating.PG_12,-1,Demographic.SEINEN,-1,"synopsis", AnimeType.ONA,"background","additional",
            setOf(Theme.BLOOD,Theme.ART),setOf(Genre.ACTION,Genre.ADVENTURE),setOf(),setOf(),setOf(), ExplicitGenre.EROTICA,LocalTime.parse("20:00"),DayOfWeek.FRIDAY,24,
            LocalDate.parse("2022-12-02"),LocalDate.parse("2023-01-01")
    )

    @Test
    fun `should create anime content entry`() = runTransactionalTest{
        val animeData = animeData.copy(studio = createStudio().id, licensor = createStudio().id, novelRelations = setOf(WorkRelation(createNovel().id,Relation.PREQUEL)), animeRelations = setOf(WorkRelation(createAnime().id,Relation.SEQUEL)))
        val securityContextCreatorID = createPlainUser().id!!
        contentRepository.findByContentKey("${ANIME_PREFIX_KEY}${animeData.title}").shouldBeNull()

        val animeContent = mono {contentService.createAnimeEntry(animeData) }
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(UsernamePasswordAuthenticationToken(securityContextCreatorID,"")))
                .awaitSingle()

        assertSoftly(contentRepository.findById(animeContent.id!!)!!) {
            verifier shouldBe null
            contentStatus shouldBe ContentStatus.PENDING_VERIFICATION
            content shouldEqualJson json.encodeToString(animeData)
            creatorID shouldBe securityContextCreatorID
            contentKey shouldBe "${ANIME_PREFIX_KEY}${animeData.title}"
            rejectionReason shouldBe null
            contentType shouldBe ContentType.ANIME
            id shouldNotBe null
        }
    }

    @Test
    fun `should throw EmptyResultDataAccessException when trying to insert anime content with studio id that does not exist`() = runTransactionalTest {
        val animeData = animeData.copy(studio = -1, licensor = createStudio().id, novelRelations = setOf(WorkRelation(createNovel().id,Relation.PREQUEL)), animeRelations = setOf(WorkRelation(createAnime().id,Relation.SEQUEL)))
        val securityContextCreatorID = createPlainUser().id!!

        shouldThrow<EmptyResultDataAccessException> {
            mono {contentService.createAnimeEntry(animeData) }
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(UsernamePasswordAuthenticationToken(securityContextCreatorID,"")))
                .awaitSingle()
        }
        contentRepository.findByContentKey("${ANIME_PREFIX_KEY}${animeData.title}").shouldBeNull()
    }

    @Test
    fun `should throw EmptyResultDataAccessException when trying to insert anime content with licensor id that does not exist`() = runTransactionalTest {
        val animeData = animeData.copy(studio = createStudio().id, licensor = -1, novelRelations = setOf(WorkRelation(createNovel().id,Relation.PREQUEL)), animeRelations = setOf(WorkRelation(createAnime().id,Relation.SEQUEL)))
        val securityContextCreatorID = createPlainUser().id!!

        shouldThrow<EmptyResultDataAccessException> {
            mono {contentService.createAnimeEntry(animeData) }
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(UsernamePasswordAuthenticationToken(securityContextCreatorID,"")))
                    .awaitSingle()
        }
        contentRepository.findByContentKey("${ANIME_PREFIX_KEY}${animeData.title}").shouldBeNull()
    }

    @Test
    fun `should throw EmptyResultDataAccessException when trying to insert anime content with novel relation that does not exist`() = runTransactionalTest{
        val animeData = animeData.copy(studio = createStudio().id, licensor = createStudio().id, novelRelations = setOf(WorkRelation(-1,Relation.PREQUEL)))
        val securityContextCreatorID = createPlainUser().id!!

        shouldThrow<EmptyResultDataAccessException> {
            mono {contentService.createAnimeEntry(animeData) }
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(UsernamePasswordAuthenticationToken(securityContextCreatorID,"")))
                    .awaitSingle()
        }
        contentRepository.findByContentKey("${ANIME_PREFIX_KEY}${animeData.title}").shouldBeNull()

    }

    @Test
    fun `should throw EmptyResultDataAccessException when trying to insert anime content with anime relation that does not exist`() = runTransactionalTest{
        val animeData = animeData.copy(studio = createStudio().id, licensor = createStudio().id, animeRelations = setOf(WorkRelation(-1,Relation.PREQUEL)))
        val securityContextCreatorID = createPlainUser().id!!

        shouldThrow<EmptyResultDataAccessException> {
            mono {contentService.createAnimeEntry(animeData) }
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(UsernamePasswordAuthenticationToken(securityContextCreatorID,"")))
                    .awaitSingle()
        }

        contentRepository.findByContentKey("${ANIME_PREFIX_KEY}${animeData.title}").shouldBeNull()
    }

    @Test
    fun `should throw EmptyResultDataAccessException when trying to insert anime content with character relation for character that does not exist`() = runTransactionalTest{
        val animeData = animeData.copy(studio = createStudio().id, licensor = createStudio().id, characters = setOf(AnimeCharacter(-1,createPerson().id,CharacterRole.PROTAGONIST)))
        val securityContextCreatorID = createPlainUser().id!!

        shouldThrow<EmptyResultDataAccessException> {
            mono {contentService.createAnimeEntry(animeData) }
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(UsernamePasswordAuthenticationToken(securityContextCreatorID,"")))
                    .awaitSingle()
        }

        contentRepository.findByContentKey("${ANIME_PREFIX_KEY}${animeData.title}").shouldBeNull()
    }

    @Test
    fun `should throw EmptyResultDataAccessException when trying to insert anime content with character relation for voice actor that does not exist`() = runTransactionalTest {
        val animeData = animeData.copy(studio = createStudio().id, licensor = createStudio().id, characters = setOf(AnimeCharacter(createCharacter().id,-1,CharacterRole.PROTAGONIST)))
        val securityContextCreatorID = createPlainUser().id!!

        shouldThrow<EmptyResultDataAccessException> {
            mono {contentService.createAnimeEntry(animeData) }
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(UsernamePasswordAuthenticationToken(securityContextCreatorID,"")))
                    .awaitSingle()
        }

        contentRepository.findByContentKey("${ANIME_PREFIX_KEY}${animeData.title}").shouldBeNull()
    }

    @Test
    fun `should throw DuplicateKeyException when trying to insert anime content with title that is already used`() = runTransactionalTest{
        val securityContextCreatorID = createPlainUser().id!!
        val animeData = animeData.copy(studio = createStudio().id, licensor = createStudio().id)
        contentRepository.findByContentKey("${ANIME_PREFIX_KEY}${animeData.title}").shouldBeNull()

        mono {contentService.createAnimeEntry(animeData) }
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(UsernamePasswordAuthenticationToken(securityContextCreatorID,"")))
                .awaitSingle()

        shouldThrow<DuplicateKeyException> {
            mono {contentService.createAnimeEntry(animeData) }
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(UsernamePasswordAuthenticationToken(securityContextCreatorID,"")))
                    .awaitSingle()
        }
    }

    @Test
    fun `should throw EmptyResultDataAccessException when trying to verify anime content that does not exist`() = runTransactionalTest{
        shouldThrow<EmptyResultDataAccessException> {
            mono { contentService.verifyAnime(-1) }.contextWrite(ReactiveSecurityContextHolder.withAuthentication(UsernamePasswordAuthenticationToken(createPlainUser().id,""))).awaitSingle()
        }
    }

    @MethodSource("allContentStatusesExceptionUnderVerification")
    @ParameterizedTest
    fun `should throw IllegalStateException when trying to verify anime that is not under verification`(notUnderVerificationStatus: ContentStatus) = runTransactionalTest{
        val creatorID = createPlainUser().id!!
        val content = contentRepository.save(Content(creatorID,ContentType.ANIME,"{}","",notUnderVerificationStatus))

        shouldThrow<IllegalStateException> {
            mono { contentService.verifyAnime(content.id!!) }.contextWrite(ReactiveSecurityContextHolder.withAuthentication(UsernamePasswordAuthenticationToken(creatorID,""))).awaitSingle()
        }
    }

    @Test
    fun `should throw AccessDeniedException when trying to verify anime and caller is not the same user as the person who is in charge of anime verification`() = runTransactionalTest{
        val creatorID = createPlainUser().id!!
        val verifierID = createPlainUser().id!!
        val callerID = createPlainUser().id!!
        val content = contentRepository.save(Content(creatorID,ContentType.ANIME,"{}","",ContentStatus.UNDER_VERIFICATION, verifier = verifierID))

        shouldThrow<AccessDeniedException> {
            mono { contentService.verifyAnime(content.id!!) }.contextWrite(ReactiveSecurityContextHolder.withAuthentication(UsernamePasswordAuthenticationToken(callerID,""))).awaitSingle()
        }
    }

    @Test
    fun `should verify anime successfully`() = runTransactionalTest{
        val creatorID = createPlainUser().id!!
        val verifierID = createPlainUser().id!!
        val content = contentRepository.save(Content(creatorID,ContentType.ANIME,json.encodeToString(animeData),"",ContentStatus.UNDER_VERIFICATION, verifier = verifierID))

        mono { contentService.verifyAnime(content.id!!) }.contextWrite(ReactiveSecurityContextHolder.withAuthentication(UsernamePasswordAuthenticationToken(verifierID,""))).awaitSingle()

        contentRepository.findById(content.id!!)!!.contentStatus shouldBe ContentStatus.VERIFIED
    }
}