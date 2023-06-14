package com.arslan.animeshka.repository

import com.arslan.animeshka.entity.Image
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface ImageRepository : CoroutineCrudRepository<Image,Long>{

    fun findAllByContentID(id: Long) : Flow<Image>

}