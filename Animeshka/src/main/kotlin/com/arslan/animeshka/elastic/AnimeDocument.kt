package com.arslan.animeshka.elastic

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field

@Document(indexName = "ANIME")
class AnimeDocument(
    @Field
    val title: String,

    @Field
    val japaneseTitle: String,

    @Id
    val id: Long
)