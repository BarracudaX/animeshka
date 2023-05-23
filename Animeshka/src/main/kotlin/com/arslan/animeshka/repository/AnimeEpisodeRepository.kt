package com.arslan.animeshka.repository

import com.arslan.animeshka.entity.AnimeEpisode
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface AnimeEpisodeRepository : CoroutineCrudRepository<AnimeEpisode,Long>