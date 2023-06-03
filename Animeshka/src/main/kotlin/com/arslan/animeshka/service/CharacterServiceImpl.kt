package com.arslan.animeshka.service

import com.arslan.animeshka.entity.Character
import com.arslan.animeshka.CharacterContent
import com.arslan.animeshka.entity.Content
import com.arslan.animeshka.repository.CharacterRepository
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class CharacterServiceImpl(
    private val contentService: ContentService,
    private val characterRepository: CharacterRepository,
    private val imageService: ImageService
) : CharacterService {

    override suspend fun createCharacterEntry(character: CharacterContent, poster: FilePart) : Content {
        val posterPath = imageService.saveImage(poster).toString()

        return contentService.createCharacterEntry(character.copy(posterPath = posterPath))
    }

    override suspend fun verifyCharacter(contentID: Long) {
        val characterContent= contentService.verifyCharacter(contentID)
        with(characterContent){ characterRepository.save(Character(characterName,japaneseName,description,characterRole,posterPath,id!!).apply { isNewEntity = true })}
    }


}