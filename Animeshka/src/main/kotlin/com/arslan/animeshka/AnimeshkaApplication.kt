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
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime
import kotlinx.datetime.toKotlinLocalDate
import org.javers.core.Javers
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.security.crypto.password.PasswordEncoder
import java.math.BigDecimal
import java.time.Duration
import java.time.LocalDate

@SpringBootApplication
class AnimeshkaApplication

fun main(args: Array<String>) : Unit = runBlocking{
    val context = runApplication<AnimeshkaApplication>(*args)
    val userService = context.getBean(UserService::class.java)
    val userRepository = context.getBean(UserRepository::class.java)
    val passwordEncoder = context.getBean(PasswordEncoder::class.java)
    val studioService = context.getBean(StudioService::class.java)

    userRepository.save(User("Test_Anime_Admin","Test_Anime_Admin","AnimeAdmin","anime@admin.com",passwordEncoder.encode("Pass123!"),UserRole.ANIME_ADMINISTRATOR))
    userService.register(UserRegistration("test@email.com","Pass123!","Pass123!","TestUser","test","test"))
    studioService.createStudio(StudioEntry("Test","Test",LocalDate.now().toKotlinLocalDate()))
    val javers = context.getBean(Javers::class.java)
}
