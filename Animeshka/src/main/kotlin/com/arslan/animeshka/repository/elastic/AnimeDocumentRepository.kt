package com.arslan.animeshka.repository.elastic

import com.arslan.animeshka.elastic.AnimeDocument
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.data.domain.Pageable
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate
import org.springframework.data.elasticsearch.core.SearchPage
import org.springframework.data.elasticsearch.core.query.Criteria
import org.springframework.data.elasticsearch.core.query.CriteriaQuery
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository
import org.springframework.stereotype.Component

interface AnimeDocumentRepository : ReactiveElasticsearchRepository<AnimeDocument,Long>, CustomizedAnimeDocumentRepository

interface CustomizedAnimeDocumentRepository{

    suspend fun findAnime(searchInput: String, pageable: Pageable) : SearchPage<AnimeDocument>

}

@Component
class CustomizedAnimeDocumentRepositoryImpl(private val reactiveElasticsearchTemplate: ReactiveElasticsearchTemplate) : CustomizedAnimeDocumentRepository{
    override suspend fun findAnime(searchInput: String, pageable: Pageable): SearchPage<AnimeDocument> {
        val criteria = Criteria.where("title").matches(searchInput).or("japaneseField").matches(searchInput)

        val query = CriteriaQuery(criteria,pageable)

        return reactiveElasticsearchTemplate.searchForPage(query,AnimeDocument::class.java).awaitSingle()
    }

}