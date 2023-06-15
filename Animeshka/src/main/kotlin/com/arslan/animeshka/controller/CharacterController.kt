package com.arslan.animeshka.controller

import com.arslan.animeshka.BasicCharacterDTO
import com.arslan.animeshka.CharacterContent
import com.arslan.animeshka.ImageType
import com.arslan.animeshka.PagedBasicCharacterDTO
import com.arslan.animeshka.entity.Character
import com.arslan.animeshka.entity.Image
import com.arslan.animeshka.service.CharacterService
import com.arslan.animeshka.service.ContentService
import com.arslan.animeshka.service.ImageService
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

@RequestMapping("/character")
@RestController
class CharacterController(private val characterService: CharacterService,private val imageService: ImageService,private val contentService: ContentService) {

    @PostMapping
    suspend fun postCharacter(@RequestPart("data") character: CharacterContent, @RequestPart("images") images: Flux<FilePart>,@RequestPart("poster") poster: FilePart) : ResponseEntity<Unit>{
        val content = contentService.createCharacterEntry(character)
        imageService.saveImages(images,poster,content)
        return ResponseEntity.ok(Unit)
    }


    @GetMapping("/search")
    suspend fun searchCharacters(@RequestParam("searchKey") searchKey:String,pageable: Pageable) : ResponseEntity<PagedBasicCharacterDTO> {
        val resultWithoutImages = characterService.searchCharacters(searchKey,pageable)

        val resultWithImages = with(resultWithoutImages){
            val enrichedContent = content.map { character -> character.enrichWith(imageService.findContentImages(character.id)) }
            PagedBasicCharacterDTO(enrichedContent,hasNext,hasPrevious)
        }

        return ResponseEntity.ok(resultWithImages)
    }

    private suspend fun BasicCharacterDTO.enrichWith(images: Flow<Image>) : BasicCharacterDTO{
        return images.fold(this){ character,nextImage ->
            when(nextImage.imageType){
                ImageType.POSTER ->
                    if(character.posterID != null){
                        throw IllegalStateException("Character with id ${character.id} must have one poster but instead has at least two with ids ${character.posterID} and ${nextImage.id}")
                    }else{
                        character.copy(posterID = nextImage.id)
                    }
                ImageType.GALLERY -> character.copy(images = character.images + nextImage.id!!)
            }
        }
    }


}