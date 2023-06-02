package com.arslan.animeshka.service

import com.arslan.animeshka.entity.Image
import com.arslan.animeshka.repository.ImageRepository
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.exists

@Transactional
@Service
class ImageServiceImpl(private val imageRepository: ImageRepository,@Value("\${image.path.location}") private val location: Path) : ImageService {

    private val allowedExtensions = setOf("png","jpeg","jpg")

    override suspend fun saveImage(image: FilePart): Path {
        val imagePath = getImagePath(image)

        if(imagePath.exists()) throw IllegalStateException("Image already exists on the path $imagePath.")

        image.transferTo(imagePath).awaitFirstOrNull()
        return imagePath
    }

    private suspend fun getImagePath(image: FilePart) : Path {
        val imageID = imageRepository.save(Image()).id
        val extension = image.filename().substring(image.filename().indexOf(".")+1)

        if(extension !in allowedExtensions) throw IllegalArgumentException("Invalid image extension. Allowed extensions: $allowedExtensions.")

        return location.resolve(Path("$imageID.${extension}"))
    }


}