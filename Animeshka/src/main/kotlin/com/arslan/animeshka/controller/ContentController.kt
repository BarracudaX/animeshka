package com.arslan.animeshka.controller

import com.arslan.animeshka.service.ModerationService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/content")
@RestController
class ContentController(private val moderationService: ModerationService) {
    
    @PutMapping("/{contentID}/accept")
    suspend fun acceptContentForModeration(@PathVariable contentID: Long) : ResponseEntity<Unit>{
        moderationService.acceptModeration(contentID)

        return ResponseEntity.ok(Unit)
    }

    @PutMapping("/{contentID}/reject")
    suspend fun rejectContent(@PathVariable contentID: Long,@RequestBody reason: String) : ResponseEntity<Unit>{
        moderationService.rejectContent(contentID,reason)

        return ResponseEntity.ok(Unit)
    }
}