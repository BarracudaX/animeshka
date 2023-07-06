package com.arslan.animeshka.elastic

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType

@Document(indexName = "studios")
data class StudioDocument(
        @Field(type = FieldType.Text)
        val studioName: String,

        @Field(type = FieldType.Text)
        val japaneseName: String?,

        @Id
        val id: Long
)