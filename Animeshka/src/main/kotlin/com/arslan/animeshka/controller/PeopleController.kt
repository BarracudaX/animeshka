package com.arslan.animeshka.controller

import com.arslan.animeshka.PersonContent
import com.arslan.animeshka.service.PeopleService
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/person")
class PeopleController(private val peopleService: PeopleService) {

    @PostMapping
    suspend fun createPersonEntry(@RequestPart("data") personContent: PersonContent, @RequestPart("image") image: FilePart) : ResponseEntity<Unit>{
        peopleService.createPersonEntry(personContent,image)

        return ResponseEntity.ok(Unit)
    }

}