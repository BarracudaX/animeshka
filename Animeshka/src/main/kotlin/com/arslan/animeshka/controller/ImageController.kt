package com.arslan.animeshka.controller

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.readBytes
import kotlin.io.path.relativeTo

@RequestMapping
@RestController
class ImageController {

    @Value("\${image.path.location}")
    private lateinit var imageLocation: Path

    @GetMapping("/poster/{image}")
    suspend fun getPoster(@PathVariable image: String) : DataBuffer {
        val path = imageLocation.resolve(Path(image))
        val data = withContext(Dispatchers.IO){
            path.readBytes()
        }
        return DefaultDataBufferFactory.sharedInstance.wrap(data)
    }

}