package com.arslan.animeshka.service

import com.arslan.animeshka.ImageExtension
import com.arslan.animeshka.ImageType
import com.arslan.animeshka.entity.Content
import com.arslan.animeshka.entity.Image
import com.arslan.animeshka.repository.ImageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.withContext
import net.coobird.thumbnailator.Thumbnails
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ResourceLoader
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferFactory
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import java.awt.image.BufferedImage
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.deleteExisting
import kotlin.io.path.exists
import kotlin.io.path.readBytes

@Transactional
@Service
class ImageServiceImpl(private val imageRepository: ImageRepository,@Value("\${image.path.location}") private val location: Path,private val resourceLoader: ResourceLoader) : ImageService {

    private val allowedExtensions = ImageExtension.values()

    override suspend fun saveImages(images: Flux<FilePart>,poster: FilePart, content: Content) {
        images.asFlow().collect { filePart ->
            saveImage(filePart,content,ImageType.GALLERY)
        }
        saveImage(poster,content,ImageType.POSTER)
    }

    override fun findContentImages(id: Long): Flow<Image> = imageRepository.findAllByContentID(id)
    override suspend fun getImageData(imageID: Long): DataBuffer {
        val image = imageRepository.findById(imageID) ?: throw EmptyResultDataAccessException("Image with id $imageID was not found.",1)
        val path = location.resolve(Path("$imageID.${image.imageExtension.name.lowercase()}"))
        val data = withContext(Dispatchers.IO){
            path.readBytes()
        }

        return DefaultDataBufferFactory.sharedInstance.wrap(data)
    }

    override suspend fun getPlaceHolder(): DataBuffer {
        val data = withContext(Dispatchers.IO){
            resourceLoader.getResource("classpath:static/placeholder-image.png").contentAsByteArray
        }
        return DefaultDataBufferFactory.sharedInstance.wrap(data)
    }

    private suspend fun saveImage(filePart: FilePart,content: Content,imageType: ImageType){
        val extensionName = filePart.filename().substring(filePart.filename().indexOf(".")+1).uppercase()
        val extension = ImageExtension.valueOf(extensionName)

        if(extension !in allowedExtensions) throw IllegalArgumentException("Invalid image extension. Allowed extensions: $allowedExtensions.")

        val image = imageRepository.save(Image(content.id!!,imageType,extension))

        val tempImagePath = location.resolve(Path("temp_${image.id!!}.${extension.name.lowercase()}"))
        val imagePath = location.resolve(Path("${image.id}.${extension.name.lowercase()}"))

        filePart.transferTo(tempImagePath).awaitSingleOrNull()

        withContext(Dispatchers.IO){
            Thumbnails
                    .of(tempImagePath.toFile())
                    .size(225,320)
                    .keepAspectRatio(true)
                    .outputFormat("JPEG")
                    .toFile(imagePath.toFile())
        }
        tempImagePath.deleteExisting()
    }

}