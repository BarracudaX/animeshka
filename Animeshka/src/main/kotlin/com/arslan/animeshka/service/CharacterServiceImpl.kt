package com.arslan.animeshka.service

import com.arslan.animeshka.entity.Character
import com.arslan.animeshka.CharacterContent
import com.arslan.animeshka.entity.Content
import com.arslan.animeshka.repository.CharacterRepository
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class CharacterServiceImpl(
    private val characterRepository: CharacterRepository,
    private val messageSource: MessageSource
) : CharacterService {

    override suspend fun insertCharacter(characterContent: CharacterContent) {
        with(characterContent){ characterRepository.save(Character(characterName,japaneseName,description, posterPath,id!!).apply { isNewEntity = true })}
    }

    override suspend fun findByName(name: String): Character {
        val character = characterRepository.findByCharacterNameOrJapaneseName(name,name) ?: throw EmptyResultDataAccessException(messageSource.getMessage("character.not.found.by.name.message",arrayOf(name),LocaleContextHolder.getLocale()),1)

        val posterPath = "/poster/${character.posterPath.substring(character.posterPath.lastIndexOf("/")+1)}"

        return character.copy(posterPath = posterPath)
    }


}