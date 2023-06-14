package com.arslan.animeshka.service

import com.arslan.animeshka.*
import com.arslan.animeshka.annotation.ClearImages
import com.arslan.animeshka.entity.Content
import com.arslan.animeshka.entity.Studio
import com.arslan.animeshka.entity.User
import com.arslan.animeshka.repository.*
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotBeEmpty
import kotlinx.datetime.toKotlinLocalDate
import kotlinx.serialization.decodeFromString
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.http.HttpHeaders
import java.time.LocalDate
import kotlin.io.path.Path
import kotlin.io.path.exists

class  AnimeServiceITest @Autowired constructor (
        private val studioService: StudioService,
) : AbstractServiceITest(){

    private val animeContent = AnimeContent(
            "test_anime_title","jp_test_anime_title",AnimeStatus.AIRING,SeriesRating.PG_12,-1,Demographic.SEINEN,-1,
            "test_synopsis",AnimeType.ONA,"test_bg","test_ai",setOf(Theme.ADULT_CAST,Theme.BLOOD),setOf(Genre.ACTION,Genre.COMEDY),
            airedAt = LocalDate.now().minusDays(3).toKotlinLocalDate(), duration = 25, explicitGenre = ExplicitGenre.EROTICA, finishedAt = LocalDate.now().toKotlinLocalDate()
    )

    private fun Content.assertAnimeContentPendingVerification(expectedCreator: User, animeContent: AnimeContent){
        assertSoftly(this){
            contentType shouldBe NewContentType.ANIME
            contentStatus shouldBe ContentStatus.PENDING_VERIFICATION
            verifier.shouldBeNull()
            rejectionReason.shouldBeNull()
            creatorID shouldBe expectedCreator.id
            contentKey shouldBe "${ANIME_PREFIX_KEY}${animeContent.title}"
        }
    }

    private fun AnimeContent.assertAnimeContent(expectedAnimeContent: AnimeContent){
        assertSoftly(this) {
            title shouldBe expectedAnimeContent.title
            japaneseTitle shouldBe expectedAnimeContent.japaneseTitle
            animeRelations shouldContainExactlyInAnyOrder expectedAnimeContent.animeRelations
            novelRelations shouldContainExactlyInAnyOrder expectedAnimeContent.novelRelations
            characters shouldContainExactlyInAnyOrder expectedAnimeContent.characters
            additionalInfo shouldBe expectedAnimeContent.additionalInfo
            background shouldBe expectedAnimeContent.background
            synopsis shouldBe expectedAnimeContent.synopsis
            airedAt shouldBe expectedAnimeContent.airedAt
            finishedAt shouldBe expectedAnimeContent.finishedAt
            duration shouldBe expectedAnimeContent.duration
            airingDay shouldBe expectedAnimeContent.airingDay
            airingTime shouldBe expectedAnimeContent.airingTime
            demographic shouldBe expectedAnimeContent.demographic
            explicitGenre shouldBe expectedAnimeContent.explicitGenre
            animeType shouldBe expectedAnimeContent.animeType
            licensor shouldBe expectedAnimeContent.licensor
            studio shouldBe expectedAnimeContent.studio
            rating shouldBe expectedAnimeContent.rating
            status shouldBe expectedAnimeContent.status
            genres shouldContainExactlyInAnyOrder expectedAnimeContent.genres
            themes shouldContainExactlyInAnyOrder expectedAnimeContent.themes
        }
    }

    /**
     * Creates new studio.
     * Must be called inside transaction to be rolledback.
     */
    private suspend fun createStudio() : Studio {
        val content = studioService.createStudio(StudioContent("test_studio_name","jp_test_studio_name",LocalDate.now().toKotlinLocalDate()))
        moderationService.acceptModeration(content.id!!)
        return studioService.verifyStudio(content.id!!)
    }

}