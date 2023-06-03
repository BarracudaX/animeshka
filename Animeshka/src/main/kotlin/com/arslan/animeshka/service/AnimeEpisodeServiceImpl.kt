package com.arslan.animeshka.service

import com.arslan.animeshka.AnimeEpisodeEntry
import com.arslan.animeshka.NewContentType
import com.arslan.animeshka.entity.Content
import com.arslan.animeshka.repository.ANIME_EPISODE_PREFIX
import com.arslan.animeshka.repository.AnimeEpisodeRepository
import com.arslan.animeshka.repository.ContentRepository
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class AnimeEpisodeServiceImpl(
    private val animeEpisodeRepository: AnimeEpisodeRepository,
    private val contentRepository: ContentRepository,
    private val json: Json
) : AnimeEpisodeService {

    override suspend fun createAnimeEpisodeEntry(animeEpisodeEntry: AnimeEpisodeEntry) {
        val content = json.encodeToString(animeEpisodeEntry)
        val creatorID = ReactiveSecurityContextHolder.getContext().awaitFirst().authentication.name.toLong()
        contentRepository.save(Content(creatorID, NewContentType.EPISODE,content,"${ANIME_EPISODE_PREFIX}${animeEpisodeEntry.animeId}_${animeEpisodeEntry.episodeName}"))
    }

}