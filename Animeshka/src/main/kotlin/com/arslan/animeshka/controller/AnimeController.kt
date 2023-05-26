package com.arslan.animeshka.controller

import com.arslan.animeshka.ContentAlreadyUnderModerationException
import com.arslan.animeshka.entity.AnimeEntry
import com.arslan.animeshka.service.AnimeService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/anime")
class AnimeController(private val animeService: AnimeService) {

    @PostMapping
    suspend fun newAnime(@RequestBody anime: AnimeEntry) : ResponseEntity<Unit>{
        animeService.insertAnimeEntry(anime)

        return ResponseEntity.ok(Unit)
    }

    @PutMapping("/{contentID}/accept")
    suspend fun acceptAnimeForModeration(@PathVariable contentID: Long) : ResponseEntity<Unit>{
        return try{
            animeService.acceptModeration(contentID)
            ResponseEntity.ok(Unit)
        }catch (ex: ContentAlreadyUnderModerationException){
            ResponseEntity.status(HttpStatus.CONFLICT).build()
        }

    }
}