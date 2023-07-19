package com.arslan.animeshka.service

import com.arslan.animeshka.*
import com.arslan.animeshka.elastic.NovelDocument
import com.arslan.animeshka.entity.Content
import com.arslan.animeshka.entity.Novel
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldContainAnyOf
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import java.time.LocalDate
import java.util.*

class NovelServiceITest @Autowired constructor(private val novelService: NovelService): AbstractServiceITest() {

    private val novelContent = NovelContent("title first","japanese first","synopsis",LocalDate.now().toKotlinLocalDate(),NovelStatus.FINISHED,NovelType.NOVEL,Demographic.SEINEN,"background")
    private val novelContent2 = NovelContent("second","title second","synopsis",LocalDate.now().toKotlinLocalDate(),NovelStatus.FINISHED,NovelType.NOVEL,Demographic.SEINEN,"background")
    private val novelContent3 = NovelContent("third","title third","synopsis",LocalDate.now().toKotlinLocalDate(),NovelStatus.FINISHED,NovelType.NOVEL,Demographic.SEINEN,"background")

    @Test
    fun `should insert new novel`() = runTransactionalTest{
        val creatorID = createPlainUser().id!!
        val contentID = contentRepository.save(Content(creatorID,ContentType.NOVEL,"{}", UUID.randomUUID().toString())).id!!
        val characters = listOf(createCharacter(),createCharacter())
        val animeRelations = listOf(createAnime(),createAnime())
        val novelRelations = listOf(createNovel(),createNovel())
        val novelContent = NovelContent(
                "title first",
                "japanese first",
                "synopsis",
                LocalDate.now().toKotlinLocalDate(),
                NovelStatus.FINISHED,
                NovelType.NOVEL,Demographic.SEINEN,
                "background",
                characters.map{ NovelCharacter(it.id,randomCharacterRole()) }.toSet(),
                animeRelations.map { WorkRelation(it.id,randomRelation()) }.toSet(),
                novelRelations.map { WorkRelation(it.id,randomRelation()) }.toSet(),
                setOf(Theme.BLOOD,Theme.ADULT_CAST),
                setOf(Genre.GIRLS_LOVE,Genre.ACTION),
                null,
                null,
                LocalDate.now().plusDays(4).toKotlinLocalDate(),
                10,
                2,
                contentID
        )

        novelService.insertNovel(novelContent)

        val novel = novelRepository.findByTitle(novelContent.title)!!

        assertSoftly(novel) {
            title shouldBe novelContent.title
            japaneseTitle shouldBe novelContent.japaneseTitle
            synopsis shouldBe novelContent.synopsis
            published shouldBe novelContent.published.toJavaLocalDate()
            novelStatus shouldBe novelContent.novelStatus
            novelType shouldBe novelContent.novelType
            demographic shouldBe novelContent.demographic
            background shouldBe novelContent.background
            explicitGenre shouldBe novelContent.explicitGenre
            magazine shouldBe novelContent.magazine
            finished shouldBe novelContent.finished?.toJavaLocalDate()
            chapters shouldBe novelContent.chapters
            volumes shouldBe novelContent.volumes
            genres() shouldContainExactlyInAnyOrder novelContent.genres
            themes() shouldContainExactlyInAnyOrder novelContent.themes
            characters() shouldContainExactlyInAnyOrder novelContent.characters
            animeRelations() shouldContainExactlyInAnyOrder novelContent.animeRelations
            novelRelations() shouldContainExactlyInAnyOrder novelContent.novelRelations
        }
    }

    @Test
    fun `should search for novels and return the result as a page`() = runTransactionalTest{
        val creatorID = createPlainUser().id!!
        val novel = with(novelContent.copy(id = contentRepository.save(Content(creatorID,ContentType.NOVEL,"{}",UUID.randomUUID().toString())).id!!)){
            novelRepository.save(Novel(title,japaneseTitle,synopsis,published.toJavaLocalDate(),novelStatus,novelType,demographic,explicitGenre,magazine,background = background, chapters = chapters, volumes = volumes, finished = finished?.toJavaLocalDate(), id = id!!).apply { isNewEntity = true })
        }
        val novel2 = with(novelContent2.copy(id = contentRepository.save(Content(creatorID,ContentType.NOVEL,"{}",UUID.randomUUID().toString())).id!!)){
            novelRepository.save(Novel(title,japaneseTitle,synopsis,published.toJavaLocalDate(),novelStatus,novelType,demographic,explicitGenre,magazine,background = background, chapters = chapters, volumes = volumes, finished = finished?.toJavaLocalDate(), id = id!!).apply { isNewEntity = true })
        }
        val novel3 = with(novelContent3.copy(id = contentRepository.save(Content(creatorID,ContentType.NOVEL,"{}",UUID.randomUUID().toString())).id!!)){
            novelRepository.save(Novel(title,japaneseTitle,synopsis,published.toJavaLocalDate(),novelStatus,novelType,demographic,explicitGenre,magazine,background = background, chapters = chapters, volumes = volumes, finished = finished?.toJavaLocalDate(), id = id!!).apply { isNewEntity = true })
        }
        novelDocumentRepository.save(NovelDocument(novel.title,novel.japaneseTitle,novel.synopsis,novel.id)).awaitSingle()
        novelDocumentRepository.save(NovelDocument(novel2.title,novel2.japaneseTitle,novel2.synopsis,novel2.id)).awaitSingle()
        novelDocumentRepository.save(NovelDocument(novel3.title,novel3.japaneseTitle,novel3.synopsis,novel3.id)).awaitSingle()

        assertSoftly(novelService.searchNovels("title", Pageable.ofSize(2))){
            content shouldContainAnyOf listOf(
                    BasicNovelDTO(novel.title,novel.japaneseTitle,novel.synopsis,novel.published.toKotlinLocalDate(),novel.novelStatus,novel.novelType,novel.demographic,novel.background,novel.finished?.toKotlinLocalDate(),novel.id),
                    BasicNovelDTO(novel2.title,novel2.japaneseTitle,novel2.synopsis,novel2.published.toKotlinLocalDate(),novel2.novelStatus,novel2.novelType,novel2.demographic,novel2.background,novel2.finished?.toKotlinLocalDate(),novel2.id),
                    BasicNovelDTO(novel3.title,novel3.japaneseTitle,novel3.synopsis,novel3.published.toKotlinLocalDate(),novel3.novelStatus,novel3.novelType,novel3.demographic,novel3.background,novel3.finished?.toKotlinLocalDate(),novel3.id)
            )
            hasNext shouldBe true
            hasPrevious shouldBe false
        }

        assertSoftly(novelService.searchNovels("first", Pageable.ofSize(2))){
            content shouldContainExactly listOf(BasicNovelDTO(novel.title,novel.japaneseTitle,novel.synopsis,novel.published.toKotlinLocalDate(),novel.novelStatus,novel.novelType,novel.demographic,novel.background,novel.finished?.toKotlinLocalDate(),novel.id), )
            hasNext shouldBe false
            hasPrevious shouldBe false
        }

    }
}
