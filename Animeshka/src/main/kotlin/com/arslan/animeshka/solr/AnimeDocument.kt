package com.arslan.animeshka.solr

import org.springframework.data.annotation.Id
import org.springframework.data.solr.core.mapping.Indexed
import org.springframework.data.solr.core.mapping.SolrDocument

@SolrDocument(collection = "ANIME")
data class AnimeDocument(

    @Indexed
    val title: String,

    @Indexed
    val japaneseTitle: String,

    @Id
    val id: Long
)