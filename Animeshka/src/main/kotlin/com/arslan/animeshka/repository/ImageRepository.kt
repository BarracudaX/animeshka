package com.arslan.animeshka.repository

import com.arslan.animeshka.entity.Image
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface ImageRepository : CoroutineCrudRepository<Image,Long>