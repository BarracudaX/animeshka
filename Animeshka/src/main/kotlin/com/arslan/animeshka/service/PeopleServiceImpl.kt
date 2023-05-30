package com.arslan.animeshka.service

import com.arslan.animeshka.PersonEntry
import com.arslan.animeshka.NewContentType
import com.arslan.animeshka.entity.UnverifiedContent
import com.arslan.animeshka.repository.PERSON_PREFIX_KEY
import com.arslan.animeshka.repository.PeopleRepository
import com.arslan.animeshka.repository.UnverifiedNewContentRepository
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class PeopleServiceImpl(
    private val peopleRepository: PeopleRepository,
    private val json: Json,
    private val contentRepository: UnverifiedNewContentRepository
) : PeopleService {
    
    override suspend fun createPersonEntry(personEntry: PersonEntry) {
        val content = json.encodeToString(personEntry)
        val creatorID = ReactiveSecurityContextHolder.getContext().awaitFirst().authentication.name.toLong()
        contentRepository.save(UnverifiedContent(creatorID,
            NewContentType.PERSON,"${PERSON_PREFIX_KEY}${personEntry.firstName}_${personEntry.lastName}_(${personEntry.givenName}_${personEntry.familyName})",content,))
    }
    
}