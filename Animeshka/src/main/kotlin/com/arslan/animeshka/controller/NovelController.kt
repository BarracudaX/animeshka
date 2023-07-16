package com.arslan.animeshka.controller

import com.arslan.animeshka.BasicNovelDTO
import com.arslan.animeshka.ImageType
import com.arslan.animeshka.PagedBasicNovelDTO
import com.arslan.animeshka.entity.Image
import com.arslan.animeshka.service.ImageService
import com.arslan.animeshka.service.NovelService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.fold
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/novel")
class NovelController(private val novelService: NovelService,private val imageService: ImageService) {

    @GetMapping("/search")
    suspend fun findNovel(@RequestParam("searchKey") searchInput: String, pageable: Pageable) : ResponseEntity<PagedBasicNovelDTO>{
        val resultWithoutImages = novelService.searchNovels(searchInput,pageable)
        val resultWithImages = with(resultWithoutImages){
            val enrichedContent = content.map { novel -> novel.enrichWithImages(imageService.findContentImages(novel.id)) }
            PagedBasicNovelDTO(enrichedContent,hasNext,hasPrevious)
        }
        return ResponseEntity.ok(resultWithImages)
    }

    private suspend fun BasicNovelDTO.enrichWithImages(images: Flow<Image>) : BasicNovelDTO{
        return images.fold(this){ novel,nextImage ->
            when(nextImage.imageType){
                ImageType.POSTER ->
                    if(novel.posterID != null){
                        throw IllegalStateException("Novel with id ${novel.id} must have one poster but instead has at least two with ids ${novel.posterID} and ${nextImage.id}")
                    }else{
                        novel.copy(posterID = nextImage.id)
                    }
                ImageType.GALLERY -> novel.copy(images = novel.images + nextImage.id!!)
            }
        }
    }

}