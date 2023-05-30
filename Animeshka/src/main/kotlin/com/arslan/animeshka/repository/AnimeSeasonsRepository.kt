package com.arslan.animeshka.repository

import com.arslan.animeshka.entity.AnimeSeason
import com.arslan.animeshka.Season
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface AnimeSeasonsRepository : CoroutineCrudRepository<AnimeSeason,Long>{

    suspend fun findBySeasonAndYear(season: Season, year: Int) : AnimeSeason

}