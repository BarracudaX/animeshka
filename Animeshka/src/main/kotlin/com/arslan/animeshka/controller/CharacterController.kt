package com.arslan.animeshka.controller

import com.arslan.animeshka.CharacterContent
import com.arslan.animeshka.service.CharacterService
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/character")
@RestController
class CharacterController(private val characterService: CharacterService) {

    @PostMapping
    suspend fun postCharacter(@RequestPart("data") character: CharacterContent, @RequestPart("poster") poster: FilePart) : ResponseEntity<Unit>{
        characterService.createCharacterEntry(character,poster)
        return ResponseEntity.ok(Unit)
    }



}