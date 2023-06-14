package com.arslan.animeshka.controller

import com.arslan.animeshka.service.ImageService
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.nio.file.Path

@RequestMapping("/image")
@RestController
class ImageController(private val imageService: ImageService) {

    @Value("\${image.path.location}")
    private lateinit var imageLocation: Path

    @GetMapping("/{imageID}")
    suspend fun getImage(@PathVariable imageID: Long) : DataBuffer {
        return imageService.getImageData(imageID)
    }

    @GetMapping("/place_holder", produces = [MediaType.IMAGE_JPEG_VALUE,MediaType.IMAGE_PNG_VALUE])
    suspend fun getPlaceHolder() : DataBuffer{
        return imageService.getPlaceHolder()
    }

}