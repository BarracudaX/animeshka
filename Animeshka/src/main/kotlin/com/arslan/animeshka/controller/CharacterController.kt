package com.arslan.animeshka.controller

import com.arslan.animeshka.CharacterContent
import com.arslan.animeshka.entity.Character
import com.arslan.animeshka.service.CharacterService
import com.arslan.animeshka.service.ContentService
import com.arslan.animeshka.service.ImageService
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import kotlin.io.path.deleteExisting

@RequestMapping("/character")
@RestController
class CharacterController(private val characterService: CharacterService,private val imageService: ImageService,private val contentService: ContentService) {

    @PostMapping
    suspend fun postCharacter(@RequestPart("data") character: CharacterContent, @RequestPart("images") images: Flux<FilePart>,@RequestPart("poster") poster: FilePart) : ResponseEntity<Unit>{
        val content = contentService.createCharacterEntry(character)
        imageService.saveImages(images,poster,content)
        return ResponseEntity.ok(Unit)
    }

    @GetMapping("/name")
    suspend fun findCharacterByName(@RequestParam name: String) : ResponseEntity<Character>{
        return ResponseEntity.ok(characterService.findByName(name))
    }


}