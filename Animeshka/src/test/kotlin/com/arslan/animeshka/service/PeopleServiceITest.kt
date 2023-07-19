package com.arslan.animeshka.service

import com.arslan.animeshka.BasicPersonDTO
import com.arslan.animeshka.ContentType
import com.arslan.animeshka.PersonContent
import com.arslan.animeshka.elastic.PersonDocument
import com.arslan.animeshka.entity.Content
import com.arslan.animeshka.entity.Person
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainAnyOf
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable

class PeopleServiceITest @Autowired constructor(private val peopleService: PeopleService) : AbstractServiceITest() {

    private val personContent = PersonContent("test first","last first","family first","given first")
    private val personContent2 = PersonContent("second name","second test","familii","anything")
    private val personContent3 = PersonContent("third","third","test family","something else")
    private val personContent4 = PersonContent("fourth","fourth","fourth","fourth test")

    @Test
    fun `should allow searching people and return result as a page`() = runTransactionalTest{
        val userID = createPlainUser().id!!
        val person = with(personContent.copy(id = contentRepository.save(Content(userID,ContentType.PERSON,"{}","1")).id!!)){
            peopleRepository.save(Person(firstName,lastName,familyName,givenName,description,birthDate?.toJavaLocalDate(),id!!).apply { isNewEntity = true })
        }
        val person2 = with(personContent2.copy(id = contentRepository.save(Content(userID,ContentType.PERSON,"{}","2")).id!!)){
            peopleRepository.save(Person(firstName,lastName,familyName,givenName,description,birthDate?.toJavaLocalDate(),id!!).apply { isNewEntity = true })
        }
        val person3 = with(personContent3.copy(id = contentRepository.save(Content(userID,ContentType.PERSON,"{}","3")).id!!)){
            peopleRepository.save(Person(firstName,lastName,familyName,givenName,description,birthDate?.toJavaLocalDate(),id!!).apply { isNewEntity = true })
        }
        val person4 = with(personContent4.copy(id = contentRepository.save(Content(userID,ContentType.PERSON,"{}","4")).id!!)){
            peopleRepository.save(Person(firstName,lastName,familyName,givenName,description,birthDate?.toJavaLocalDate(),id!!).apply { isNewEntity = true })
        }
        with(person){ peopleDocumentRepository.save(PersonDocument(firstName,lastName,familyName,givenName,description,id)).awaitFirst() }
        with(person2){ peopleDocumentRepository.save(PersonDocument(firstName,lastName,familyName,givenName,description,id)).awaitFirst() }
        with(person3){ peopleDocumentRepository.save(PersonDocument(firstName,lastName,familyName,givenName,description,id)).awaitFirst() }
        with(person4){ peopleDocumentRepository.save(PersonDocument(firstName,lastName,familyName,givenName,description,id)).awaitFirst() }

        val result = peopleService.searchPeople("test", Pageable.ofSize(2))

        assertSoftly(result) {
            content shouldHaveSize 2
            content shouldContainAnyOf listOf(
                    BasicPersonDTO(person.firstName,person.lastName,person.familyName,person.givenName,person.description,person.birthDate?.toKotlinLocalDate(),person.id),
                    BasicPersonDTO(person2.firstName,person2.lastName,person2.familyName,person2.givenName,person2.description,person2.birthDate?.toKotlinLocalDate(),person2.id),
                    BasicPersonDTO(person3.firstName,person3.lastName,person3.familyName,person3.givenName,person3.description,person3.birthDate?.toKotlinLocalDate(),person3.id),
                    BasicPersonDTO(person4.firstName,person4.lastName,person4.familyName,person4.givenName,person4.description,person4.birthDate?.toKotlinLocalDate(),person4.id)
            )
            hasNext shouldBe true
            hasPrevious shouldBe false
        }

        assertSoftly(peopleService.searchPeople("family", Pageable.ofSize(2))) {
            content shouldHaveSize 2
            hasNext shouldBe false
            hasPrevious shouldBe false
            content shouldContainExactlyInAnyOrder listOf(
                    BasicPersonDTO(person.firstName,person.lastName,person.familyName,person.givenName,person.description,person.birthDate?.toKotlinLocalDate(),person.id),
                    BasicPersonDTO(person3.firstName,person3.lastName,person3.familyName,person3.givenName,person3.description,person3.birthDate?.toKotlinLocalDate(),person3.id),
            )
        }
    }

    @Test
    fun `should insert new person into database`() = runTransactionalTest{
        val userID = createPlainUser().id!!
        val personContent = personContent.copy(id = contentRepository.save(Content(userID,ContentType.PERSON,"{}","1")).id!!)
        peopleRepository.findAll().toList().shouldBeEmpty()

        peopleService.insertPerson(personContent)

        val people = peopleRepository.findAll().toList()
        people shouldHaveSize 1
        assertSoftly(people[0]) {
            firstName shouldBe personContent.firstName
            lastName shouldBe personContent.lastName
            givenName shouldBe personContent.givenName
            familyName shouldBe personContent.familyName
            description shouldBe personContent.description
            birthDate shouldBe personContent.birthDate?.toJavaLocalDate()
            id shouldBe personContent.id
        }
    }
}