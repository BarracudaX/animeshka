package com.arslan.animeshka.repository.elastic

import com.arslan.animeshka.elastic.StudioDocument
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.data.domain.Pageable
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate
import org.springframework.data.elasticsearch.core.SearchPage
import org.springframework.data.elasticsearch.core.query.Criteria
import org.springframework.data.elasticsearch.core.query.CriteriaQuery
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository

interface StudioDocumentRepository : ReactiveElasticsearchRepository<StudioDocument,Long>, CustomizedStudioDocumentRepository

interface CustomizedStudioDocumentRepository{

    suspend fun findStudios(searchInput: String, pageable: Pageable) : SearchPage<StudioDocument>

}

class CustomizedStudioDocumentRepositoryImpl(private val reactiveElasticsearchTemplate: ReactiveElasticsearchTemplate) : CustomizedStudioDocumentRepository{
    override suspend fun findStudios(searchInput: String, pageable: Pageable): SearchPage<StudioDocument> {
        val criteria = Criteria.where("studioName").matches(searchInput).or("japaneseName").matches(searchInput)

        return reactiveElasticsearchTemplate
                .searchForPage(CriteriaQuery(criteria,pageable),StudioDocument::class.java)
                .awaitSingle()
    }

}
