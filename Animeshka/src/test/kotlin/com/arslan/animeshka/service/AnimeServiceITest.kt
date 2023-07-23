package com.arslan.animeshka.service

import com.arslan.animeshka.*
import com.arslan.animeshka.elastic.AnimeDocument
import com.arslan.animeshka.entity.Content
import com.arslan.animeshka.entity.User
import com.arslan.animeshka.repository.*
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldContainAnyOf
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaLocalTime
import kotlinx.datetime.toKotlinLocalDate
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import java.time.LocalDate
import java.util.*
import kotlin.math.exp

class  AnimeServiceITest @Autowired constructor (private val animeService: AnimeService) : AbstractServiceITest(){

    private val animeContent = AnimeContent(
            "first test","any_japanese",AnimeStatus.AIRING,SeriesRating.PG_12,-1,Demographic.SEINEN,-1,
            "test_synopsis",AnimeType.ONA,"test_bg","test_ai",setOf(Theme.ADULT_CAST,Theme.BLOOD),setOf(Genre.ACTION,Genre.COMEDY),
            airedAt = LocalDate.now().minusDays(3).toKotlinLocalDate(), duration = 25, explicitGenre = ExplicitGenre.EROTICA, finishedAt = LocalDate.now().toKotlinLocalDate()
    )

    private val animeContent2 = AnimeContent(
        "any_title","second test",AnimeStatus.FINISHED,SeriesRating.R_15,-1,Demographic.JOSEI,-1,
        "test_synopsis_2",AnimeType.SPECIAL,"test_bg_2","test_ai_2",setOf(Theme.ANTHROPOMORPHIC,Theme.CHILDCARE),setOf(Genre.ADVENTURE,Genre.FANTASY),
        airedAt = LocalDate.now().minusDays(33).toKotlinLocalDate(), duration = 22, finishedAt = LocalDate.now().minusDays(10).toKotlinLocalDate()
    )

    @Test
    fun `should insert anime content`() = runTransactionalTest{
        val animeContent = animeContent.copy(
            studio = createStudio().id,
            licensor = createStudio().id,
            novelRelations = setOf(WorkRelation(createNovel().id,Relation.SEQUEL)),
            animeRelations = setOf(WorkRelation(createAnime().id,Relation.PREQUEL)),
            characters = setOf(AnimeCharacter(createCharacter().id,createPerson().id,CharacterRole.ANTAGONIST)),
            id = contentRepository.save(Content(createPlainUser().id!!,ContentType.ANIME,"{}","")).id!!
        )

        val animeID = animeService.insertAnime(animeContent).id

        assertSoftly(animeRepository.findById(animeID)!!) {
            studio shouldBe animeContent.studio
            licensor shouldBe animeContent.licensor
            title shouldBe animeContent.title
            japaneseTitle shouldBe animeContent.japaneseTitle
            status shouldBe animeContent.status
            rating shouldBe animeContent.rating
            demographic shouldBe animeContent.demographic
            synopsis shouldBe animeContent.synopsis
            animeType shouldBe animeContent.animeType
            background shouldBe animeContent.background
            additionalInformation shouldBe animeContent.additionalInfo
            publishedAt shouldBe animeContent.airedAt?.toJavaLocalDate()
            duration shouldBe animeContent.duration
            explicitGenre shouldBe animeContent.explicitGenre
            finishedAt shouldBe animeContent.finishedAt?.toJavaLocalDate()
            airingDay shouldBe animeContent.airingDay
            airingTime shouldBe animeContent.airingTime?.toJavaLocalTime()
            animeRank shouldBe null
            genres() shouldContainExactlyInAnyOrder animeContent.genres
            themes() shouldContainExactlyInAnyOrder animeContent.themes
            novelRelations() shouldContainExactlyInAnyOrder animeContent.novelRelations
            animeRelations() shouldContainExactlyInAnyOrder animeContent.animeRelations
            characters() shouldContainExactlyInAnyOrder animeContent.characters
        }
    }

    @Test
    fun `should search for anime and return result in pages`() = runTransactionalTest{
        val animeContent = animeContent.copy(
            studio = createStudio().id,
            licensor = createStudio().id,
            id = contentRepository.save(Content(createPlainUser().id!!,ContentType.ANIME,"{}", UUID.randomUUID().toString())).id!!
        )
        val animeContent2 = animeContent2.copy(
            studio = createStudio().id,
            licensor = createStudio().id,
            id = contentRepository.save(Content(createPlainUser().id!!,ContentType.ANIME,"{}",UUID.randomUUID().toString())).id!!
        )
        val expectedAnime = listOf(
            with(animeContent){ BasicAnimeDTO(title,japaneseTitle,status,demographic,synopsis,animeType,id!!,background,airedAt,finishedAt) },
            with(animeContent2){ BasicAnimeDTO(title,japaneseTitle,status,demographic,synopsis,animeType,id!!,background,airedAt,finishedAt) }
        )
        animeService.insertAnime(animeContent)
        animeService.insertAnime(animeContent2)
        animeDocumentRepository.save(AnimeDocument(animeContent.title,animeContent.japaneseTitle,animeContent.synopsis,animeContent.id!!)).awaitSingle()
        animeDocumentRepository.save(AnimeDocument(animeContent2.title,animeContent2.japaneseTitle,animeContent2.synopsis,animeContent2.id!!)).awaitSingle()

        assertSoftly(animeService.search("test", Pageable.ofSize(1))) {
            hasNext shouldBe true
            hasPrevious shouldBe false
            content shouldContainAnyOf expectedAnime
        }

        assertSoftly(animeService.search("test", Pageable.ofSize(2))) {
            hasNext shouldBe false
            hasPrevious shouldBe false
            content shouldContainExactlyInAnyOrder expectedAnime
        }

        assertSoftly(animeService.search("first", Pageable.ofSize(1))) {
            hasNext shouldBe false
            hasPrevious shouldBe false
            content shouldHaveSize 1
            content[0] shouldBe expectedAnime[0]
        }
    }
}