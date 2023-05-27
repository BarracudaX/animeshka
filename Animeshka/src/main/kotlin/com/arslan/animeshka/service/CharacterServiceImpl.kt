package com.arslan.animeshka.service

import com.arslan.animeshka.entity.Character
import com.arslan.animeshka.entity.NewContentType
import com.arslan.animeshka.entity.UnverifiedNewContent
import com.arslan.animeshka.repository.CHARACTER_PREFIX_KEY
import com.arslan.animeshka.repository.CharacterRepository
import com.arslan.animeshka.repository.UnverifiedNewContentRepository
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class CharacterServiceImpl(
    private val contentRepository: UnverifiedNewContentRepository,
    private val characterRepository: CharacterRepository,
    private val json: Json
) : CharacterService {

    override suspend fun createCharacterEntry(character: Character) {
        val content = json.encodeToString(character)
        val creatorID = ReactiveSecurityContextHolder.getContext().awaitFirst().authentication.name.toLong()
        contentRepository.save(UnverifiedNewContent(creatorID,NewContentType.CHARACTER,content,"${CHARACTER_PREFIX_KEY}${character.characterName}"))
    }


}