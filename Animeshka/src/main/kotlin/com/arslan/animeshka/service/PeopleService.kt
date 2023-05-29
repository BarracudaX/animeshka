package com.arslan.animeshka.service

import com.arslan.animeshka.PersonEntry

interface PeopleService {

    suspend fun createPersonEntry(personEntry: PersonEntry)

}