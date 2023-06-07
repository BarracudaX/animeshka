package com.arslan.animeshka.repository.elastic

import com.arslan.animeshka.elastic.AnimeDocument
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository

interface AnimeDocumentRepository : ReactiveElasticsearchRepository<AnimeDocument,Long>