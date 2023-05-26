package com.arslan.animeshka.repository

interface AnimeCharacterRepository {

    suspend fun createAnimeCharacterEntry(animeID : Long,characterID: Long,voiceActorID: Long)

}