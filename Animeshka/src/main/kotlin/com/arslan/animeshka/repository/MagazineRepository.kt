package com.arslan.animeshka.repository

import com.arslan.animeshka.entity.Magazine
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface MagazineRepository : CoroutineCrudRepository<Magazine,Long>{

    suspend fun findByMagazineName(magazineName: String) : Magazine?

}