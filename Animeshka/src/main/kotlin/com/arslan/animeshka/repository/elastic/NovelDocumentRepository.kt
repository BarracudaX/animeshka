package com.arslan.animeshka.repository.elastic

import com.arslan.animeshka.elastic.NovelDocument
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.data.domain.Pageable
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate
import org.springframework.data.elasticsearch.core.SearchPage
import org.springframework.data.elasticsearch.core.query.Criteria
import org.springframework.data.elasticsearch.core.query.CriteriaQuery
import org.springframework.data.elasticsearch.core.query.CriteriaQueryBuilder
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository
import org.springframework.stereotype.Component

interface NovelDocumentRepository : ReactiveElasticsearchRepository<NovelDocument,Long>, CustomizedNovelDocumentRepository

interface CustomizedNovelDocumentRepository{

    suspend fun findNovel(searchInput: String,pageable: Pageable) : SearchPage<NovelDocument>

}

@Component
class CustomizedNovelDocumentRepositoryImpl(private val reactiveElasticsearchTemplate: ReactiveElasticsearchTemplate) : CustomizedNovelDocumentRepository{

    override suspend fun findNovel(searchInput: String, pageable: Pageable): SearchPage<NovelDocument> {
        val criteria = Criteria.where("title").matches(searchInput).or("japaneseTitle").matches(searchInput)

        val query = CriteriaQuery(criteria,pageable)

        return reactiveElasticsearchTemplate.searchForPage(query,NovelDocument::class.java).awaitSingle()
    }

}

