package com.arslan.animeshka.repository

import com.arslan.animeshka.entity.Magazine
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface MagazineRepository : CoroutineCrudRepository<Magazine,Long>