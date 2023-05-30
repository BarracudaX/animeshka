package com.arslan.animeshka.repository

import com.arslan.animeshka.entity.Anime
import kotlinx.coroutines.flow.Flow
import org.springframework.data.relational.core.sql.LockMode
import org.springframework.data.relational.repository.Lock
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface AnimeRepository : CoroutineCrudRepository<Anime,Long>{


}