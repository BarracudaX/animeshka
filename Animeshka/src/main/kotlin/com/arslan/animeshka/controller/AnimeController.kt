package com.arslan.animeshka.controller

import com.arslan.animeshka.AnimeDTO
import com.arslan.animeshka.BasicAnimeDTO
import com.arslan.animeshka.AnimeContent
import com.arslan.animeshka.PagedBasicAnimeDTO
import com.arslan.animeshka.elastic.AnimeDocument
import com.arslan.animeshka.repository.elastic.AnimeDocumentRepository
import com.arslan.animeshka.service.AnimeService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/anime")
class AnimeController(private val animeService: AnimeService,private val animeDocumentRepository: AnimeDocumentRepository) {

    @PostMapping
    suspend fun newAnime(@RequestPart("data") anime: AnimeContent, @RequestPart("image") image: FilePart) : ResponseEntity<Unit>{
        animeService.createUnverifiedAnime(anime,image)

        return ResponseEntity.ok(Unit)
    }

    @PutMapping("/verify/{animeID}")
    suspend fun verifyAnime(@PathVariable animeID: Long) : ResponseEntity<Unit>{
        val anime = animeService.verifyAnimeEntry(animeID)
        with(anime){ animeDocumentRepository.save(AnimeDocument(title,japaneseTitle, synopsis,id)).awaitSingle() }
        return ResponseEntity.ok(Unit)
    }

    @PutMapping
    suspend fun updateAnime(@RequestBody anime: AnimeDTO) : ResponseEntity<Unit>{
        animeService.updateAnime(anime)
        return ResponseEntity.ok(Unit)
    }

    @GetMapping("/title")
    suspend fun findByTitle(@RequestParam("title") title: String,pageable: Pageable) : ResponseEntity<PagedBasicAnimeDTO> = ResponseEntity.ok(animeService.findAnimeByTitle(title,pageable))

}