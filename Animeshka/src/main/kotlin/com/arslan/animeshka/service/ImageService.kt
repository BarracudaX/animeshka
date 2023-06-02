package com.arslan.animeshka.service

import org.springframework.http.codec.multipart.FilePart
import java.nio.file.Path

interface ImageService {

    suspend fun saveImage(image: FilePart) : Path

}