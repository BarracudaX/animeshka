package com.arslan.animeshka.service

import com.arslan.animeshka.CharacterContent
import com.arslan.animeshka.PagedBasicCharacterDTO
import com.arslan.animeshka.entity.Character
import org.springframework.data.domain.Pageable

interface CharacterService {

    suspend fun insertCharacter(characterContent: CharacterContent) : Character
    suspend fun searchCharacters(searchKey: String, pageable: Pageable): PagedBasicCharacterDTO

}