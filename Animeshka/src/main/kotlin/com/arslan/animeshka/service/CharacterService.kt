package com.arslan.animeshka.service

import com.arslan.animeshka.entity.Character

interface CharacterService {

    suspend fun createCharacterEntry(character: Character)

}