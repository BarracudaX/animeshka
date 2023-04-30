package com.arslan.animeshka.repository

import com.arslan.animeshka.entity.AnimeSeason
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface AnimeSeasonsRepository : CoroutineCrudRepository<AnimeSeason,Long>