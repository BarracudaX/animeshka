package com.arslan.animeshka.elastic

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType

@Document(indexName = "people")
data class PersonDocument(
        @Field(type = FieldType.Text)
        val firstName: String,

        @Field(type = FieldType.Text)
        val lastName: String,

        @Field(type = FieldType.Text)
        val familyName: String? = null,

        @Field(type = FieldType.Text)
        val givenName: String? = null,

        @Field(type = FieldType.Text)
        val description: String = "",

        @Id
        val id: Long
)