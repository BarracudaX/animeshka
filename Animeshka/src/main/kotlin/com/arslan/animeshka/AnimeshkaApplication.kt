package com.arslan.animeshka

import com.arslan.animeshka.entity.*
import com.arslan.animeshka.repository.AnimeRepository
import com.arslan.animeshka.repository.AnimeSeasonsRepository
import com.arslan.animeshka.repository.StudioRepository
import com.arslan.animeshka.repository.UserRepository
import com.arslan.animeshka.service.StudioService
import com.arslan.animeshka.service.UserService
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.toKotlinLocalDate
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.math.BigDecimal
import java.time.Duration
import java.time.LocalDate

@SpringBootApplication
class AnimeshkaApplication

fun main(args: Array<String>) : Unit = runBlocking{
    val context = runApplication<AnimeshkaApplication>(*args)
    val userService = context.getBean(UserService::class.java)
    val studioService = context.getBean(StudioService::class.java)
    userService.register(UserRegistration("test@email.com","Pass123!","Pass123!","TestUser","test","test"))
    studioService.createStudio(StudioEntry("Test","Test",LocalDate.now().toKotlinLocalDate()))
}
