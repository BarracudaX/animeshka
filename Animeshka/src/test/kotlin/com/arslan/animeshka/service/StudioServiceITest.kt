package com.arslan.animeshka.service

import com.arslan.animeshka.BasicStudioDTO
import com.arslan.animeshka.ContentType
import com.arslan.animeshka.StudioContent
import com.arslan.animeshka.elastic.StudioDocument
import com.arslan.animeshka.entity.Content
import com.arslan.animeshka.entity.Studio
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainAnyOf
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import java.time.LocalDate

class StudioServiceITest @Autowired constructor(private val studioService: StudioService) : AbstractServiceITest() {

    private val studioContent = StudioContent("TEST","JAPANESE TEST", LocalDate.now().toKotlinLocalDate())
    private val studioContent2 = StudioContent("ANOTHER","JAPANESE NAME", LocalDate.now().toKotlinLocalDate())

    @Test
    fun `should insert new studio into database`() = runTransactionalTest{
        val studioContent = studioContent.copy(id = contentRepository.save(Content(createPlainUser().id!!, ContentType.STUDIO,"{}","")).id!!)
        studioRepository.findAll().toList().shouldBeEmpty()

        studioService.insertStudio(studioContent)

        val studios = studioRepository.findAll().toList()
        studios.shouldHaveSize(1)
        assertSoftly(studios[0]) {
            studioName shouldBe studioContent.studioName
            japaneseName shouldBe studioContent.japaneseName
            established shouldBe studioContent.established.toJavaLocalDate()
        }
    }

    @Test
    fun `should find studios by search key and return the result as a page`() = runTransactionalTest {
        val userID = createPlainUser().id!!
        val studio = with(studioContent.copy(id = contentRepository.save(Content(userID, ContentType.STUDIO,"{}","1")).id!!)){
            studioRepository.save(Studio(studioName,japaneseName,established.toJavaLocalDate(),id!!).apply { isNewEntity = true })
        }
        val studio2 = with(studioContent2.copy(id = contentRepository.save(Content(userID, ContentType.STUDIO,"{}","")).id!!)){
            studioRepository.save(Studio(studioName,japaneseName,established.toJavaLocalDate(),id!!).apply { isNewEntity = true })
        }
        studioDocumentRepository.save(StudioDocument(studio.studioName,studio.japaneseName,studio.id)).awaitSingle()
        studioDocumentRepository.save(StudioDocument(studio2.studioName,studio2.japaneseName,studio2.id)).awaitSingle()

        val result = studioService.search("JAPANESE", Pageable.ofSize(1))

        assertSoftly(result) {
            content shouldContainAnyOf listOf(BasicStudioDTO(studio.studioName,studio.japaneseName,studio.id!!),BasicStudioDTO(studio2.studioName,studio2.japaneseName,studio2.id!!))
            hasPrevious shouldBe false
            hasNext shouldBe true
        }

        val result2 = studioService.search("ANOTHER", Pageable.ofSize(10))
        assertSoftly(result2) {
            content shouldContainExactly listOf(BasicStudioDTO(studio2.studioName,studio2.japaneseName,studio2.id!!))
            hasPrevious shouldBe false
            hasNext shouldBe false
        }

        studioDocumentRepository.deleteAll().awaitSingleOrNull()
    }


}