package com.arslan.animeshka

import com.arslan.animeshka.entity.User
import com.arslan.animeshka.repository.UserRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.runBlocking
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AnimeshkaApplication

fun main(args: Array<String>) = runBlocking{
    val context = runApplication<AnimeshkaApplication>(*args)
}
