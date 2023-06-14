package com.arslan.animeshka.controller

import com.arslan.animeshka.PersonContent
import com.arslan.animeshka.service.ContentService
import com.arslan.animeshka.service.ImageService
import com.arslan.animeshka.service.PeopleService
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import kotlin.io.path.deleteExisting

@RestController
@RequestMapping("/person")
class PeopleController(private val peopleService: PeopleService,private val imageService: ImageService,private val contentService: ContentService) {

    @PostMapping
    suspend fun createPersonEntry(@RequestPart("data") personContent: PersonContent, @RequestPart("images") images: Flux<FilePart>,@RequestPart("poster") poster: FilePart) : ResponseEntity<Unit>{
        val content = contentService.createPersonEntry(personContent)

        imageService.saveImages(images,poster,content)

        return ResponseEntity.ok(Unit)
    }

}