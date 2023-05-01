package com.arslan.animeshka.repository

import com.arslan.animeshka.entity.User
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.reactive.ReactiveSortingRepository
import reactor.core.publisher.Mono

interface UserRepository : CoroutineCrudRepository<User,Long> {

    suspend fun findByEmail(email: String) : User?

    suspend fun findByUsername(username: String) : User?

}