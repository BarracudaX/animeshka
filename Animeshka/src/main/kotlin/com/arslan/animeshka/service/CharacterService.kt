package com.arslan.animeshka.service

import com.arslan.animeshka.CharacterContent
import com.arslan.animeshka.entity.Character
import com.arslan.animeshka.entity.Content
import org.springframework.http.codec.multipart.FilePart

interface CharacterService {

    suspend fun createCharacterEntry(character: CharacterContent) : Content

    suspend fun verifyCharacter(contentID: Long)
    suspend fun findByName(name: String): Character

}