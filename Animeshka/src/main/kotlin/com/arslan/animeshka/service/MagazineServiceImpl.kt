package com.arslan.animeshka.service

import com.arslan.animeshka.entity.Magazine
import com.arslan.animeshka.NewContentType
import com.arslan.animeshka.entity.Content
import com.arslan.animeshka.repository.MAGAZINE_PREFIX_KEY
import com.arslan.animeshka.repository.MagazineRepository
import com.arslan.animeshka.repository.ContentRepository
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class MagazineServiceImpl(
    private val magazineRepository: MagazineRepository,
    private val contentRepository: ContentRepository,
    private val json: Json
) : MagazineService {
    override suspend fun createMagazineEntry(magazine: Magazine) {
        val content = json.encodeToString(magazine)
        val creatorID = ReactiveSecurityContextHolder.getContext().awaitFirst().authentication.name.toLong()

        contentRepository.save(Content(creatorID, NewContentType.MAGAZINE,content,"${MAGAZINE_PREFIX_KEY}${magazine.magazineName}"))
    }
}