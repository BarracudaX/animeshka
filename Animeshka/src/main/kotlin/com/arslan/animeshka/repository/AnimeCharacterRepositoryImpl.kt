package com.arslan.animeshka.repository

import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.await
import org.springframework.stereotype.Repository

@Repository
class AnimeCharacterRepositoryImpl(private val databaseClient: DatabaseClient) : AnimeCharacterRepository {

    override suspend fun createAnimeCharacterEntry(animeID: Long, characterID: Long, voiceActorID: Long) {
        databaseClient
            .sql { "INSERT INTO ANIME_CHARACTERS(character_id,anime_id,voice_actor_id) VALUES(:characterID,:animeID,:voiceActorID)" }
            .bind("characterID",characterID)
            .bind("animeID",animeID)
            .bind("voiceActorID",voiceActorID)
            .await()
    }

}