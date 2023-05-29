package com.arslan.animeshka.service

import com.arslan.animeshka.AnimeDTO
import com.arslan.animeshka.entity.ContentChange

interface ContentChangeService {

    suspend fun insertAnimeChanges(currentAnimeState: AnimeDTO, newAnimeState: AnimeDTO)

}