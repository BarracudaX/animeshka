package com.arslan.animeshka.service

import com.arslan.animeshka.PagedBasicPersonDTO
import com.arslan.animeshka.PersonContent
import com.arslan.animeshka.entity.Person
import org.springframework.data.domain.Pageable

interface PeopleService {

    suspend fun insertPerson(personContent: PersonContent) : Person
    suspend fun searchPeople(searchKey: String, pageable: Pageable): PagedBasicPersonDTO

}