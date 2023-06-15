package com.arslan.animeshka.repository.elastic

import com.arslan.animeshka.elastic.AnimeDocument
import com.arslan.animeshka.elastic.PersonDocument
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.data.domain.Pageable
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate
import org.springframework.data.elasticsearch.core.SearchPage
import org.springframework.data.elasticsearch.core.query.Criteria
import org.springframework.data.elasticsearch.core.query.CriteriaQuery
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository
import org.springframework.stereotype.Component

interface PeopleDocumentRepository : ReactiveElasticsearchRepository<PersonDocument,Long>, CustomizedPeopleDocumentRepository

interface CustomizedPeopleDocumentRepository{

    suspend fun findPeople(searchKey: String,pageable: Pageable) : SearchPage<PersonDocument>

}

@Component
class CustomizedPeopleDocumentRepositoryImpl(private val reactiveElasticsearchTemplate: ReactiveElasticsearchTemplate) : CustomizedPeopleDocumentRepository{

    override suspend fun findPeople(searchKey: String, pageable: Pageable): SearchPage<PersonDocument> {
        val criteria = Criteria
                .where("firstName").matches(searchKey)
                .or("lastName").matches(searchKey)
                .or("familyName").matches(searchKey)
                .or("givenName").matches(searchKey)

        val query = CriteriaQuery(criteria,pageable)

        return reactiveElasticsearchTemplate.searchForPage(query, PersonDocument::class.java).awaitSingle()
    }

}