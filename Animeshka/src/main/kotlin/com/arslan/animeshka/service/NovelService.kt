package com.arslan.animeshka.service

import com.arslan.animeshka.BasicNovelDTO
import com.arslan.animeshka.UnverifiedNovel
import com.arslan.animeshka.entity.UnverifiedContent
import org.springframework.http.codec.multipart.FilePart

interface NovelService {

    suspend fun findByTitle(title: String) : BasicNovelDTO

    suspend fun createNovel(novel: UnverifiedNovel, poster: FilePart) : UnverifiedContent

    suspend fun verifyNovel(novelID: Long)
}