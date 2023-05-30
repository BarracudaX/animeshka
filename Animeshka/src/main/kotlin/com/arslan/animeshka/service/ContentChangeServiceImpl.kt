package com.arslan.animeshka.service

import com.arslan.animeshka.AnimeDTO
import com.arslan.animeshka.entity.ContentChange
import com.arslan.animeshka.ContentChangeOperation
import com.arslan.animeshka.ContentChangeStatus
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

    private val animeReplaceOnlyPaths = listOf("title","background","japaneseTitle","status","rating","studio","demographic","licensor","synopsis","animeType","additionalInfo","explicitGenre","airingTime","airingDay","duration","airedAt","finishedAt")

    override suspend fun insertAnimeChanges(currentAnimeState: AnimeDTO, newAnimeState: AnimeDTO) {
        val currentAnimeStateJsonNode = objectMapper.convertValue(currentAnimeState,JsonNode::class.java)
        val newAnimeStateJsonNode = objectMapper.convertValue(newAnimeState,JsonNode::class.java)
        val creatorID = ReactiveSecurityContextHolder.getContext().awaitFirst().authentication.name.toLong()

        val changes = JsonDiff.asJson(currentAnimeStateJsonNode,newAnimeStateJsonNode,EnumSet.of(DiffFlags.ADD_EXPLICIT_REMOVE_ADD_ON_REPLACE))

        for(change in changes){
            val path = change.get("path")!!.asText().replace("/",".").replace(Regex("[.]\\d+"),"").removePrefix(".")
            val operation = ContentChangeOperation.valueOf(change.get("op")!!.asText().uppercase())
            val needsReplace = path in animeReplaceOnlyPaths
            val contentChange = if(needsReplace && operation == ContentChangeOperation.ADD){
                ContentChange( currentAnimeState.id,path, ContentChangeOperation.REPLACE,change.get("value")!!.asText(),creatorID)
            }else if(needsReplace){
                continue
            }else{
                ContentChange( currentAnimeState.id,path, operation,change.get("value")!!.asText(),creatorID)
            }
            contentChangeRepository.save(contentChange)
        }

    }

}