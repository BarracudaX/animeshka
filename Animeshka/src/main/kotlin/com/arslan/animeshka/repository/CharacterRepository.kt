package com.arslan.animeshka.repository

import com.arslan.animeshka.entity.Character
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface CharacterRepository : CoroutineCrudRepository<Character,Long>