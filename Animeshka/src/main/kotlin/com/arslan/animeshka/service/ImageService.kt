package com.arslan.animeshka.service

import com.arslan.animeshka.entity.Content
import com.arslan.animeshka.entity.Image
import kotlinx.coroutines.flow.Flow
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.codec.multipart.FilePart
import reactor.core.publisher.Flux
import java.nio.file.Path

interface ImageService {

    suspend fun saveImages(images: Flux<FilePart>,poster: FilePart, content: Content)
    fun findContentImages(id: Long) : Flow<Image>

    suspend fun getImageData(imageID: Long) : DataBuffer
    suspend fun getPlaceHolder(): DataBuffer

}