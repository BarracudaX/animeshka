package com.arslan.animeshka.elastic

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType

@Document(indexName = "characters")
data class CharacterDocument(
        @Field(type = FieldType.Text)
        val characterName: String,

        @Field(type = FieldType.Text)
        val description: String,

        @Field(type = FieldType.Text)
        val japaneseName: String? = null,

        @Id
        val id: Long
)