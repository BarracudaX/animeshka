package com.arslan.animeshka.controller

import com.arslan.animeshka.*
import com.arslan.animeshka.elastic.AnimeDocument
import com.arslan.animeshka.entity.Image
import com.arslan.animeshka.repository.elastic.AnimeDocumentRepository
import com.arslan.animeshka.service.AnimeService
import com.arslan.animeshka.service.ContentService
import com.arslan.animeshka.service.ImageService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.fold
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
import reactor.core.publisher.Flux

@RestController
@RequestMapping("/anime")
class AnimeController(
        private val animeService: AnimeService,
        private val animeDocumentRepository: AnimeDocumentRepository,
        private val imageService: ImageService,
        private val contentService: ContentService
) {

    @PostMapping
    suspend fun newAnime(@RequestPart("data") anime: AnimeContent,@RequestPart("images") images: Flux<FilePart>,@RequestPart("poster") poster: FilePart) : ResponseEntity<Unit>{
        val content = contentService.createAnimeEntry(anime)
        imageService.saveImages(images,poster,content)
        return ResponseEntity.ok(Unit)
    }

    @PutMapping("/verify/{animeID}")
    suspend fun verifyAnime(@PathVariable animeID: Long) : ResponseEntity<Unit>{
        val anime = animeService.insertAnime(contentService.verifyAnime(animeID))
        with(anime){ animeDocumentRepository.save(AnimeDocument(title,japaneseTitle, synopsis,id)).awaitSingle() }
        return ResponseEntity.ok(Unit)
    }

    @PutMapping
    suspend fun updateAnime(@RequestBody anime: AnimeDTO) : ResponseEntity<Unit>{
        animeService.updateAnime(anime)
        return ResponseEntity.ok(Unit)
    }

    @GetMapping("/search")
    suspend fun search(@RequestParam("searchKey") title: String, pageable: Pageable) : ResponseEntity<PagedBasicAnimeDTO> {
        val resultWithoutImages = animeService.findAnimeByTitle(title,pageable)
        val resultWithImages = with(resultWithoutImages){
            val enrichedContent = content.map { anime -> anime.enrichWith(imageService.findContentImages(anime.id)) }
            PagedBasicAnimeDTO(enrichedContent,hasNext,hasPrevious)
        }

        return ResponseEntity.ok(resultWithImages)
    }

    private suspend fun BasicAnimeDTO.enrichWith(images: Flow<Image>) : BasicAnimeDTO{
        return images.fold(this){ anime,nextImage ->
            when(nextImage.imageType){
                ImageType.POSTER ->
                    if(anime.posterID != null){
                        throw IllegalStateException("Anime with id ${anime.id} must have one poster but instead has at least two with ids ${anime.posterID} and ${nextImage.id}")
                    }else{
                        anime.copy(posterID = nextImage.id)
                    }
                ImageType.GALLERY -> anime.copy(images = anime.images + nextImage.id!!)
            }
        }
    }

}