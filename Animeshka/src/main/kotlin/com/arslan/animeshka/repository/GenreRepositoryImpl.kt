package com.arslan.animeshka.repository

import com.arslan.animeshka.entity.Genre
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.await
import org.springframework.stereotype.Repository

@Repository
class GenreRepositoryImpl(private val databaseClient: DatabaseClient) : GenreRepository {

    override suspend fun createAnimeGenreEntry(animeID: Long, genre: Genre) {
        databaseClient
            .sql { "INSERT INTO ANIME_GENRES(anime_id,genre) VALUES(:animeID,:genre)" }
            .bind("animeID",animeID)
            .bind("genre",genre.name)
            .await()
    }

}