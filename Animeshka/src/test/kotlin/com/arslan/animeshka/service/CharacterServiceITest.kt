package com.arslan.animeshka.service

import com.arslan.animeshka.BasicCharacterDTO
import com.arslan.animeshka.CharacterContent
import com.arslan.animeshka.ContentStatus
import com.arslan.animeshka.ContentType
import com.arslan.animeshka.elastic.CharacterDocument
import com.arslan.animeshka.entity.Content
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldContainAnyOf
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.reactor.awaitSingle
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import java.util.*

class CharacterServiceITest @Autowired constructor(private val characterService: CharacterService) : AbstractServiceITest() {

    private val characterContent = CharacterContent("first test","first japanese","any_desc")
    private val characterContent2 = CharacterContent("second","second test","any_desc")

    @Test
    fun `should insert new character`() = runTransactionalTest{
        val characterContent = characterContent.copy(id = contentRepository.save(Content(createPlainUser().id!!, ContentType.CHARACTER,"{}","", ContentStatus.VERIFIED)).id!!)

        val characterID = characterService.insertCharacter(characterContent).id

        assertSoftly(characterRepository.findById(characterID)!!) {
            characterName shouldBe characterContent.characterName
            japaneseName shouldBe characterContent.japaneseName
            description shouldBe characterContent.description
        }
    }

    @Test
    fun `should search and return characters in pages`() = runTransactionalTest{
        val characterContent = characterContent.copy(id = contentRepository.save(Content(createPlainUser().id!!, ContentType.CHARACTER,"{}", UUID.randomUUID().toString(), ContentStatus.VERIFIED)).id!!)
        val characterContent2 = characterContent2.copy(id = contentRepository.save(Content(createPlainUser().id!!, ContentType.CHARACTER,"{}",UUID.randomUUID().toString(), ContentStatus.VERIFIED)).id!!)
        characterService.insertCharacter(characterContent)
        characterService.insertCharacter(characterContent2)
        characterDocumentRepository.save(CharacterDocument(characterContent.characterName,characterContent.description,characterContent.japaneseName,characterContent.id!!)).awaitSingle()
        characterDocumentRepository.save(CharacterDocument(characterContent2.characterName,characterContent2.description,characterContent2.japaneseName,characterContent2.id!!)).awaitSingle()

        assertSoftly(characterService.searchCharacters("test", Pageable.ofSize(1))) {
            hasNext shouldBe true
            hasPrevious shouldBe false
            content shouldContainAnyOf listOf(
                BasicCharacterDTO(characterContent.characterName,characterContent.japaneseName,characterContent.description,characterContent.id!!),
                BasicCharacterDTO(characterContent2.characterName,characterContent2.japaneseName,characterContent2.description,characterContent2.id!!),
            )
        }

        assertSoftly(characterService.searchCharacters("test", Pageable.ofSize(2))) {
            hasNext shouldBe false
            hasPrevious shouldBe false
            content shouldContainExactlyInAnyOrder  listOf(
                BasicCharacterDTO(characterContent.characterName,characterContent.japaneseName,characterContent.description,characterContent.id!!),
                BasicCharacterDTO(characterContent2.characterName,characterContent2.japaneseName,characterContent2.description,characterContent2.id!!),
            )
        }
    }
}