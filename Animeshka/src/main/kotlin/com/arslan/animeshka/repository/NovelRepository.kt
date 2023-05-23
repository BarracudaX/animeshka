package com.arslan.animeshka.repository

import com.arslan.animeshka.entity.Novel
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface NovelRepository : CoroutineCrudRepository<Novel,Long>