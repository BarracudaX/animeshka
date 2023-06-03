package com.arslan.animeshka.repository

import com.arslan.animeshka.entity.Content
import org.springframework.data.relational.core.sql.LockMode
import org.springframework.data.relational.repository.Lock
import org.springframework.data.repository.kotlin.CoroutineCrudRepository


const val ANIME_PREFIX_KEY = "ANIME_"
const val CHARACTER_PREFIX_KEY = "CHARACTER_"
const val STUDIO_PREFIX_KEY = "STUDIO_"
const val PERSON_PREFIX_KEY = "PERSON_"
const val MAGAZINE_PREFIX_KEY = "MAGAZINE_"
const val ANIME_EPISODE_PREFIX = "ANIME_EPISODE_"
const val NOVEL_PREFIX_KEY = "NOVEL_"

interface ContentRepository : CoroutineCrudRepository<Content,Long>{

    @Lock(LockMode.PESSIMISTIC_WRITE)
    override suspend fun findById(id: Long) : Content?

}