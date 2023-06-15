package com.arslan.animeshka.entity

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate

@Table("PEOPLE")
data class Person(
    val firstName: String,

    val lastName: String,

    val familyName: String? = null,

    val givenName: String? = null,

    val description: String = "",

    @Column("birthdate")
    val birthDate: LocalDate? = null,

    @Id
    val id: Long
): Persistable<Long>{

    @Transient
    var isNewEntity: Boolean = false

    override fun getId(): Long = id

    override fun isNew(): Boolean = isNewEntity

}