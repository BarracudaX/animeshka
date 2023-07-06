package com.arslan.animeshka.controller

import com.arslan.animeshka.BasicStudioDTO
import com.arslan.animeshka.ImageType
import com.arslan.animeshka.PagedBasicStudioDTO
import com.arslan.animeshka.StudioContent
import com.arslan.animeshka.elastic.StudioDocument
import com.arslan.animeshka.entity.Image
import com.arslan.animeshka.repository.elastic.StudioDocumentRepository
import com.arslan.animeshka.service.ContentService
import com.arslan.animeshka.service.ImageService
import com.arslan.animeshka.service.StudioService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.reactive.awaitSingle
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
@RequestMapping("/studio")
class StudioController(private val studioService: StudioService,private val contentService: ContentService,private val studioDocumentRepository: StudioDocumentRepository,private val imageService: ImageService) {

    @PostMapping
    suspend fun createStudio(@RequestBody studioContent: StudioContent,@RequestPart("images") images: Flux<FilePart>,@RequestPart("poster") poster: FilePart) : ResponseEntity<Unit>{
        val content = contentService.createStudioEntry(studioContent)
        imageService.saveImages(images,poster,content)
        return ResponseEntity.ok(Unit)
    }

    @PutMapping("/verify/{contentID}")
    suspend fun verifyStudio(@PathVariable contentID: Long) : ResponseEntity<Unit>{
        val studioContent = contentService.verifyStudio(contentID)
        val studio = studioService.insertStudio(studioContent)
        with(studio){ studioDocumentRepository.save(StudioDocument(studioName,japaneseName,id)).awaitSingle() }
        return ResponseEntity.ok(Unit)
    }

    @GetMapping("/search")
    suspend fun search(@RequestParam("searchKey") searchKey: String,pageable: Pageable) : ResponseEntity<PagedBasicStudioDTO>{
        val resultWithoutImages = studioService.search(searchKey,pageable)
        val resultWithImages = with(resultWithoutImages){
            val enrichedContent = content.map { studio -> studio.enrichWithImages(imageService.findContentImages(studio.id)) }
            PagedBasicStudioDTO(enrichedContent,hasNext,hasPrevious)
        }

        return ResponseEntity.ok(resultWithImages)
    }

    private suspend fun BasicStudioDTO.enrichWithImages(images: Flow<Image>) : BasicStudioDTO {
        return images.fold(this) { studio, nextImage ->
            when (nextImage.imageType) {
                ImageType.POSTER ->
                    if (studio.posterID != null) {
                        throw IllegalStateException("Studio with id ${studio.id} must have one poster but instead has at least two with ids ${studio.posterID} and ${nextImage.id}")
                    } else {
                        studio.copy(posterID = nextImage.id)
                    }

                ImageType.GALLERY -> studio.copy(images = studio.images + nextImage.id!!)
            }
        }
    }

}