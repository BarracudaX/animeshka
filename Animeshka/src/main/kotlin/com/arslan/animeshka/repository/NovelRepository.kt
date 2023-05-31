package com.arslan.animeshka.repository

import com.arslan.animeshka.entity.Novel
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface NovelRepository : CoroutineCrudRepository<Novel,Long>{

    suspend fun findByTitleOrJapaneseTitle(title: String, japaneseTitle: String) : Novel?

}