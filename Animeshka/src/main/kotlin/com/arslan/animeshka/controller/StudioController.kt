package com.arslan.animeshka.controller

import com.arslan.animeshka.StudioContent
import com.arslan.animeshka.service.ContentService
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
class StudioController(private val studioService: StudioService,private val contentService: ContentService) {

    @PostMapping
    suspend fun createStudio(@RequestBody studioContent: StudioContent) : ResponseEntity<Unit>{
        contentService.createStudioEntry(studioContent)
        return ResponseEntity.ok(Unit)
    }

    @PutMapping("/verify/{contentID}")
    suspend fun verifyStudio(@PathVariable contentID: Long) : ResponseEntity<Unit>{
        val studioContent = contentService.verifyStudio(contentID)
        studioService.insertStudio(studioContent)
        return ResponseEntity.ok(Unit)
    }

}