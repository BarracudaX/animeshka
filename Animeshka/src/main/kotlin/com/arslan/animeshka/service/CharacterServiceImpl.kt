package com.arslan.animeshka.service

import com.arslan.animeshka.BasicCharacterDTO
import com.arslan.animeshka.entity.Character
import com.arslan.animeshka.CharacterContent
import com.arslan.animeshka.PagedBasicAnimeDTO
import com.arslan.animeshka.PagedBasicCharacterDTO
import com.arslan.animeshka.elastic.CharacterDocument
import com.arslan.animeshka.repository.CharacterRepository
import com.arslan.animeshka.repository.elastic.CharacterDocumentRepository
import org.springframework.context.MessageSource
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class CharacterServiceImpl(
        private val characterRepository: CharacterRepository,
        private val characterDocumentRepository: CharacterDocumentRepository
) : CharacterService {

    override suspend fun insertCharacter(characterContent: CharacterContent): Character {
        return with(characterContent) { characterRepository.save(Character(characterName, japaneseName, description, id!!).apply { isNewEntity = true }) }
    }

    override suspend fun searchCharacters(searchKey: String, pageable: Pageable): PagedBasicCharacterDTO {
        val result = characterDocumentRepository.findCharacter(searchKey,pageable)
        return with(result){
            PagedBasicCharacterDTO(searchHits.searchHits.map{ it.content.toBasicCharacterDTO() },hasNext(),hasPrevious())
        }
    }

    private suspend fun CharacterDocument.toBasicCharacterDTO() : BasicCharacterDTO{
        val character = characterRepository.findById(id)!!

        return with(character){ BasicCharacterDTO(characterName,japaneseName,description,id) }
    }

}