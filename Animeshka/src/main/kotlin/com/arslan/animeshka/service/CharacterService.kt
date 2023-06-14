package com.arslan.animeshka.service

import com.arslan.animeshka.CharacterContent
import com.arslan.animeshka.entity.Character

interface CharacterService {

    suspend fun insertCharacter(characterContent: CharacterContent)
    suspend fun findByName(name: String): Character

}