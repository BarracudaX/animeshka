package com.arslan.animeshka

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import kotlinx.coroutines.withContext
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.codec.multipart.FilePart
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

class AppFilePart(private val httpHeaders: HttpHeaders,private val file: Path) : FilePart {
    override fun name(): String {
        return headers().contentDisposition.name!!
    }

    override fun headers(): HttpHeaders = httpHeaders

    override fun content(): Flux<DataBuffer> {
        return DataBufferUtils.readByteChannel({Files.newByteChannel(file)},DefaultDataBufferFactory.sharedInstance,1024)
    }

    override fun filename(): String = httpHeaders.contentDisposition.filename!!

    override fun transferTo(dest: Path): Mono<Void> = mono{
        withContext(Dispatchers.IO) {
            Files.copy(file, dest, StandardCopyOption.REPLACE_EXISTING)
        }
        null
    }

}