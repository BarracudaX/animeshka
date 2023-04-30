package com.arslan.animeshka.repository

import com.arslan.animeshka.entity.Studio
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface StudioRepository : CoroutineCrudRepository<Studio,Long>