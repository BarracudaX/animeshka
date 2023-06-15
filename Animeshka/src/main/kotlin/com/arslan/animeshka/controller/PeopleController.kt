package com.arslan.animeshka.controller

import com.arslan.animeshka.BasicPersonDTO
import com.arslan.animeshka.ImageType
import com.arslan.animeshka.PagedBasicPersonDTO
import com.arslan.animeshka.PersonContent
import com.arslan.animeshka.entity.Image
import com.arslan.animeshka.service.ContentService
import com.arslan.animeshka.service.ImageService
import com.arslan.animeshka.service.PeopleService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.fold
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import kotlin.io.path.deleteExisting

@RestController
@RequestMapping("/person")
class PeopleController(private val peopleService: PeopleService,private val imageService: ImageService,private val contentService: ContentService) {

    @PostMapping
    suspend fun createPersonEntry(@RequestPart("data") personContent: PersonContent, @RequestPart("images") images: Flux<FilePart>,@RequestPart("poster") poster: FilePart) : ResponseEntity<Unit>{
        val content = contentService.createPersonEntry(personContent)

        imageService.saveImages(images,poster,content)

        return ResponseEntity.ok(Unit)
    }

    @GetMapping("/search")
    suspend fun searchPeople(@RequestParam("searchKey") searchKey: String,pageable: Pageable) : ResponseEntity<PagedBasicPersonDTO>{
        val resultWithoutImages = peopleService.searchPeople(searchKey,pageable)
        val resultWithImages = with(resultWithoutImages){
            val enrichedContent = content.map { person -> person.enrichWith(imageService.findContentImages(person.id)) }
            PagedBasicPersonDTO(enrichedContent,hasNext,hasPrevious)
        }

        return ResponseEntity.ok(resultWithImages)
    }

    private suspend fun BasicPersonDTO.enrichWith(images: Flow<Image>) : BasicPersonDTO{
        return images.fold(this){ person,nextImage ->
            when(nextImage.imageType){
                ImageType.POSTER ->
                    if(person.posterID != null ){
                        throw IllegalStateException("Person with id ${person.id} must have one poster but has at least two with ids ${person.posterID},${nextImage.id}")
                    }else{
                        person.copy(posterID = nextImage.id)
                    }
                ImageType.GALLERY -> person
            }
        }
    }
}