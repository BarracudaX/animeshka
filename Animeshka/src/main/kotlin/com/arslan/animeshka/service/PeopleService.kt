package com.arslan.animeshka.service

import com.arslan.animeshka.PersonContent

interface PeopleService {

    suspend fun insertPerson(personContent: PersonContent)

}