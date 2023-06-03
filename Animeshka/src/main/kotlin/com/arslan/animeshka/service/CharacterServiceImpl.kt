package com.arslan.animeshka.service

import com.arslan.animeshka.entity.Character
import com.arslan.animeshka.NewContentType
import com.arslan.animeshka.UnverifiedCharacter
import com.arslan.animeshka.entity.UnverifiedContent
import com.arslan.animeshka.repository.CHARACTER_PREFIX_KEY
import com.arslan.animeshka.repository.CharacterRepository
import com.arslan.animeshka.repository.UnverifiedNewContentRepository
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.http.codec.multipart.FilePart
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class CharacterServiceImpl(
    private val contentService: ContentService,
    private val characterRepository: CharacterRepository,
    private val imageService: ImageService
) : CharacterService {

    override suspend fun createCharacterEntry(character: UnverifiedCharacter, poster: FilePart) : UnverifiedContent {
        val posterPath = imageService.saveImage(poster).toString()

        return contentService.createCharacterEntry(character.copy(posterPath = posterPath))
    }

    override suspend fun verifyCharacter(contentID: Long) {
        val (characterData,verifiedContent) = contentService.verifyCharacter(contentID)
        with(characterData){ characterRepository.save(Character(characterName,japaneseName,description,characterRole,posterPath,verifiedContent.id).apply { isNewEntity = true })}
    }


}