package com.arslan.animeshka.controller

import com.arslan.animeshka.entity.Character
import com.arslan.animeshka.entity.NewContentType
import com.arslan.animeshka.service.CharacterService
import com.arslan.animeshka.service.ModerationService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/character")
@RestController
class CharacterController(private val characterService: CharacterService) {

    @PostMapping
    suspend fun postCharacter(@RequestBody character: Character) : ResponseEntity<Unit>{
        characterService.createCharacterEntry(character)
        return ResponseEntity.ok(Unit)
    }



}