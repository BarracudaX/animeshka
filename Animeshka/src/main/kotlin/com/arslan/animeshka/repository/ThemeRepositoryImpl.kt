package com.arslan.animeshka.repository

import com.arslan.animeshka.entity.Theme
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.await
import org.springframework.stereotype.Repository

@Repository
class ThemeRepositoryImpl(private val databaseClient: DatabaseClient) : ThemeRepository {

    override suspend fun createAnimeThemeEntry(animeID: Long, theme: Theme) {
        databaseClient
            .sql { "INSERT INTO ANIME_THEMES(anime_id,theme) VALUES(:animeID,:theme)" }
            .bind("animeID",animeID)
            .bind("theme",theme.name)
            .await()
    }

}