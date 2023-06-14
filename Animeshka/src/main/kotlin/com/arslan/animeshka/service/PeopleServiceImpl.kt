package com.arslan.animeshka.service

import com.arslan.animeshka.PersonContent
import com.arslan.animeshka.entity.Content
import com.arslan.animeshka.entity.Person
import com.arslan.animeshka.repository.PeopleRepository
import kotlinx.datetime.toJavaLocalDate
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class PeopleServiceImpl(
    private val peopleRepository: PeopleRepository
) : PeopleService {

    override suspend fun insertPerson(personContent: PersonContent) {
        with(personContent){ peopleRepository.save(Person(firstName,lastName,familyName,givenName,description,birthDate?.toJavaLocalDate(),id!!).apply { isNewEntity = true }) }
    }

}