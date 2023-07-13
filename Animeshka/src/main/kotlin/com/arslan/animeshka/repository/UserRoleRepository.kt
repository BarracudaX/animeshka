package com.arslan.animeshka.repository

import com.arslan.animeshka.entity.UserRole
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.kotlin.CoroutineSortingRepository

interface UserRoleRepository : CoroutineCrudRepository<UserRole,Long>{

    fun findAllByUserID(userID: Long) : Flow<UserRole>

}