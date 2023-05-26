package com.arslan.animeshka.repository

import com.arslan.animeshka.entity.Theme

interface ThemeRepository {

    suspend fun createAnimeThemeEntry(animeID: Long, theme: Theme)

}