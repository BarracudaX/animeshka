package com.arslan.animeshka.service

import com.arslan.animeshka.PersonContent
import com.arslan.animeshka.entity.Content
import org.springframework.http.codec.multipart.FilePart

interface PeopleService {

    suspend fun createPersonEntry(personContent: PersonContent, image: FilePart) : Content
    suspend fun verifyPerson(id: Long)

}