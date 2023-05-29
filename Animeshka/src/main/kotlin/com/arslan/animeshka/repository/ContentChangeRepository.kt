package com.arslan.animeshka.repository

import com.arslan.animeshka.entity.ContentChange
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface ContentChangeRepository : CoroutineCrudRepository<ContentChange,Long>