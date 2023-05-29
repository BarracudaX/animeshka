package com.arslan.animeshka.controller

import com.arslan.animeshka.PersonEntry
import com.arslan.animeshka.service.PeopleService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/person")
class PeopleController(private val peopleService: PeopleService) {

    @PostMapping
    suspend fun createPersonEntry(@RequestBody personEntry: PersonEntry) : ResponseEntity<Unit>{
        peopleService.createPersonEntry(personEntry)

        return ResponseEntity.ok(Unit)
    }

}