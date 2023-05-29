package com.arslan.animeshka.service

import com.arslan.animeshka.AnimeDTO
import com.arslan.animeshka.entity.ContentChange
import com.arslan.animeshka.entity.ContentChangeOperation
import com.arslan.animeshka.repository.ContentChangeRepository
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.flipkart.zjsonpatch.DiffFlags
import com.flipkart.zjsonpatch.JsonDiff
import kotlinx.coroutines.reactive.awaitFirst
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.EnumSet

@Service
@Transactional
class ContentChangeServiceImpl(
    private val contentChangeRepository: ContentChangeRepository,
    private val objectMapper: ObjectMapper
) : ContentChangeService {

    override suspend fun insertAnimeChanges(currentAnimeState: AnimeDTO, newAnimeState: AnimeDTO) {
        val currentAnimeStateJsonNode = objectMapper.convertValue(currentAnimeState,JsonNode::class.java)
        val newAnimeStateJsonNode = objectMapper.convertValue(newAnimeState,JsonNode::class.java)
        val creatorID = ReactiveSecurityContextHolder.getContext().awaitFirst().authentication.name.toLong()

        val changes = JsonDiff.asJson(currentAnimeStateJsonNode,newAnimeStateJsonNode,EnumSet.of(DiffFlags.ADD_ORIGINAL_VALUE_ON_REPLACE))

        for(change in changes){
            contentChangeRepository.save( ContentChange( currentAnimeState.id,change.get("path")!!.asText(),ContentChangeOperation.valueOf(change.get("op")!!.asText().uppercase()),change.get("value")!!.asText(),change.get("fromValue")?.asText(),creatorID) )
        }

    }

}