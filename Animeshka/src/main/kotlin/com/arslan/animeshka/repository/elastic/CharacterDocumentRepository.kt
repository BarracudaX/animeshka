package com.arslan.animeshka.repository.elastic

import com.arslan.animeshka.elastic.AnimeDocument
import com.arslan.animeshka.elastic.CharacterDocument
import com.arslan.animeshka.elastic.NovelDocument
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.data.domain.Pageable
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate
import org.springframework.data.elasticsearch.core.SearchPage
import org.springframework.data.elasticsearch.core.query.Criteria
import org.springframework.data.elasticsearch.core.query.CriteriaQuery
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository
import org.springframework.stereotype.Component

interface CharacterDocumentRepository : ReactiveElasticsearchRepository<CharacterDocument,Long>, CustomizedCharacterDocumentRepository

interface CustomizedCharacterDocumentRepository{

    suspend fun findCharacter(searchInput: String,pageable: Pageable) : SearchPage<CharacterDocument>

}

@Component
class CustomizedCharacterDocumentRepositoryImpl(private val reactiveElasticsearchTemplate: ReactiveElasticsearchTemplate) : CustomizedCharacterDocumentRepository{
    override suspend fun findCharacter(searchInput: String, pageable: Pageable) : SearchPage<CharacterDocument> {

        val criteria = Criteria.where("characterName").matches(searchInput).or("japaneseName").matches(searchInput)

        val query = CriteriaQuery(criteria,pageable)

        return reactiveElasticsearchTemplate.searchForPage(query, CharacterDocument::class.java).awaitSingle()
    }

}