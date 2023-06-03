package com.arslan.animeshka.service

import com.arslan.animeshka.BasicNovelDTO
import com.arslan.animeshka.NovelContent
import com.arslan.animeshka.entity.Content
import org.springframework.http.codec.multipart.FilePart

interface NovelService {

    suspend fun findByTitle(title: String) : BasicNovelDTO

    suspend fun createNovel(novel: NovelContent, poster: FilePart) : Content

    suspend fun verifyNovel(novelID: Long)
}