package com.arslan.animeshka.repository

import com.arslan.animeshka.entity.NewContentType
import com.arslan.animeshka.entity.UnverifiedNewContent
import org.springframework.data.relational.core.sql.LockMode
import org.springframework.data.relational.repository.Lock
import org.springframework.data.repository.kotlin.CoroutineCrudRepository


const val ANIME_PREFIX_KEY = "ANIME_"
const val CHARACTER_PREFIX_KEY = "CHARACTER_"

interface UnverifiedNewContentRepository : CoroutineCrudRepository<UnverifiedNewContent,Long>{

    @Lock(LockMode.PESSIMISTIC_WRITE)
    override suspend fun findById(id: Long) : UnverifiedNewContent?

}