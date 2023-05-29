package com.arslan.animeshka.repository

import com.arslan.animeshka.entity.Content
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface ContentRepository : CoroutineCrudRepository<Content,Long>