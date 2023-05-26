package com.arslan.animeshka.repository

import com.arslan.animeshka.entity.Relation
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.await
import org.springframework.stereotype.Repository

@Repository
class RelationRepositoryImpl(private val databaseClient: DatabaseClient) : RelationRepository {

    override suspend fun createAnimeNovelRelationEntry(animeID: Long, novelID: Long, relation: Relation) {
        databaseClient
            .sql { "INSERT INTO NOVEL_ANIME_RELATIONS(novel_id,anime_id,relation) VALUES(:novelID,:animeID,:relation)" }
            .bind("novelID",novelID)
            .bind("animeID",animeID)
            .bind("relation",relation.name)
            .await()
    }

    override suspend fun createAnimeAnimeRelationEntry(animeID: Long, relatedAnimeID: Long, relation: Relation) {
        databaseClient
            .sql { "INSERT INTO ANIME_ANIME_RELATIONS(anime_id,related_anime_id,relation) VALUES(:animeID,:relatedAnimeID,:relation)" }
            .bind("animeID",animeID)
            .bind("relatedAnimeID",relatedAnimeID)
            .bind("relation",relation.name)
            .await()
    }

}