package com.arslan.animeshka.repository

import com.arslan.animeshka.entity.Relation

interface RelationRepository {

    suspend fun createAnimeNovelRelationEntry(animeID: Long, novelID: Long, relation: Relation)

    suspend fun createAnimeAnimeRelationEntry(animeID: Long, relatedAnimeID: Long, relation: Relation)
}