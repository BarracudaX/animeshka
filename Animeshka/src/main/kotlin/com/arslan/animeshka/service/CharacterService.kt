package com.arslan.animeshka.service

import com.arslan.animeshka.UnverifiedCharacter
import com.arslan.animeshka.entity.Character
import com.arslan.animeshka.entity.UnverifiedContent
import org.springframework.http.codec.multipart.FilePart

interface CharacterService {

    suspend fun createCharacterEntry(character: UnverifiedCharacter,poster: FilePart) : UnverifiedContent

    suspend fun verifyCharacter(contentID: Long)
}