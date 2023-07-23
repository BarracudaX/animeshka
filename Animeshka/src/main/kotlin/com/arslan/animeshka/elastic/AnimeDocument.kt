package com.arslan.animeshka.elastic

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType

@Document(indexName = "anime")
data class AnimeDocument(
        @Field(type = FieldType.Text)
        val title: String,


        @Field(type = FieldType.Text, name = "japanese_title")
        val japaneseTitle: String,

        @Field(type = FieldType.Text)
        val synopsis: String,

        @Id
        val id: Long
)