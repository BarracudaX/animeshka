package com.arslan.animeshka.repository

import com.arslan.animeshka.entity.Person
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface PeopleRepository : CoroutineCrudRepository<Person,Long>