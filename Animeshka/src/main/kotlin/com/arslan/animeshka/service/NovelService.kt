package com.arslan.animeshka.service

import com.arslan.animeshka.NovelContent
import com.arslan.animeshka.PagedBasicNovelDTO
import com.arslan.animeshka.entity.Content
import com.arslan.animeshka.entity.Novel
import org.springframework.data.domain.Pageable
import org.springframework.http.codec.multipart.FilePart

interface NovelService {

    suspend fun findNovel(searchInput: String, pageable: Pageable) : PagedBasicNovelDTO

    suspend fun createNovel(novel: NovelContent) : Content

    suspend fun verifyNovel(novelID: Long) : Novel
}