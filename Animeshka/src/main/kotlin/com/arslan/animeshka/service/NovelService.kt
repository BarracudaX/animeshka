package com.arslan.animeshka.service

import com.arslan.animeshka.BasicNovelDTO
import com.arslan.animeshka.NovelDTO
import com.arslan.animeshka.UnverifiedNovel
import com.arslan.animeshka.entity.Novel
import com.arslan.animeshka.entity.UnverifiedContent

interface NovelService {

    suspend fun findByTitle(title: String) : BasicNovelDTO

    suspend fun createNovel(novel: UnverifiedNovel) : UnverifiedContent

    suspend fun verifyNovel(novelID: Long)
}