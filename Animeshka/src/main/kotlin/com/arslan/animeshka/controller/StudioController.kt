package com.arslan.animeshka.controller

import com.arslan.animeshka.UnverifiedStudio
import com.arslan.animeshka.service.StudioService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/studio")
class StudioController(private val studioService: StudioService) {

    @PostMapping
    suspend fun createStudio(@RequestBody unverifiedStudio: UnverifiedStudio) : ResponseEntity<Unit>{
        studioService.createStudio(unverifiedStudio)
        return ResponseEntity.ok(Unit)
    }

    @PutMapping("/verify/{contentID}")
    suspend fun verifyStudio(@PathVariable contentID: Long) : ResponseEntity<Unit>{
        studioService.verifyStudio(contentID)
        return ResponseEntity.ok(Unit)
    }

}