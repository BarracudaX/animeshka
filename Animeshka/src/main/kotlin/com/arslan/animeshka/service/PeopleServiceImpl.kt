package com.arslan.animeshka.service

import com.arslan.animeshka.BasicPersonDTO
import com.arslan.animeshka.PagedBasicPersonDTO
import com.arslan.animeshka.PersonContent
import com.arslan.animeshka.elastic.PersonDocument
import com.arslan.animeshka.entity.Content
import com.arslan.animeshka.entity.Person
import com.arslan.animeshka.repository.PeopleRepository
import com.arslan.animeshka.repository.elastic.PeopleDocumentRepository
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import org.springframework.data.domain.Pageable
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class PeopleServiceImpl(
        private val peopleRepository: PeopleRepository,
        private val peopleDocumentRepository: PeopleDocumentRepository
) : PeopleService {

    override suspend fun insertPerson(personContent: PersonContent) : Person {
        return with(personContent) { peopleRepository.save(Person(firstName, lastName, familyName, givenName, description, birthDate?.toJavaLocalDate(), id!!).apply { isNewEntity = true }) }
    }

    override suspend fun searchPeople(searchKey: String, pageable: Pageable): PagedBasicPersonDTO {
        val result = peopleDocumentRepository.findPeople(searchKey,pageable)
        return with(result){ PagedBasicPersonDTO(searchHits.searchHits.map { it.content.toBasicPersonDTO() },hasNext(),hasPrevious()) }
    }

    private suspend fun PersonDocument.toBasicPersonDTO() : BasicPersonDTO{
        return with(peopleRepository.findById(id)!!){ BasicPersonDTO(firstName,lastName,familyName,givenName,description,this.birthDate?.toKotlinLocalDate(),id) }
    }

}