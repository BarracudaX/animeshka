package com.arslan.animeshka.repository

import com.arslan.animeshka.entity.VerifiedContent
import org.springframework.data.relational.core.sql.LockMode
import org.springframework.data.relational.repository.Lock
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface ContentRepository : CoroutineCrudRepository<VerifiedContent,Long>{

    @Lock(LockMode.PESSIMISTIC_WRITE)
    override suspend fun findById(id: Long): VerifiedContent?
}