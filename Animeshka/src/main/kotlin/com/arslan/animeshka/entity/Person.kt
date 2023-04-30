package com.arslan.animeshka.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate

@Table("PEOPLE")
class Person(
    val firstName: String,

    val lastName: String,

    val familyName: String,

    val givenName: String,

    val description: String = "",

    val birthDate: LocalDate? = null,

    @Id
    val id: Long? = null
)