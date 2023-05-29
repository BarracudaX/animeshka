package com.arslan.animeshka.controller

import com.arslan.animeshka.AnimeDTO
import com.arslan.animeshka.UnverifiedAnime
import com.arslan.animeshka.service.AnimeService
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
    suspend fun newAnime(@RequestBody anime: UnverifiedAnime) : ResponseEntity<Unit>{
        animeService.createUnverifiedAnime(anime)

        return ResponseEntity.ok(Unit)
    }

    @PutMapping("/verify/{animeID}")
    suspend fun verifyAnime(@PathVariable animeID: Long) : ResponseEntity<Unit>{
        animeService.verifyAnimeEntry(animeID)
        return ResponseEntity.ok(Unit)
    }

    @PutMapping
    suspend fun updateAnime(@RequestBody anime: AnimeDTO) : ResponseEntity<Unit>{
        animeService.updateAnime(anime)
        return ResponseEntity.ok(Unit)
    }

}