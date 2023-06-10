package com.arslan.animeshka.repository.elastic

import com.arslan.animeshka.elastic.AnimeDocument
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface AnimeDocumentRepository : ReactiveElasticsearchRepository<AnimeDocument,Long>{

    fun findByTitleOrJapaneseTitle(title: String, japaneseTitle: String) : Flux<AnimeDocument>

}