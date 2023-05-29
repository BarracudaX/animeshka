package com.arslan.animeshka

import com.arslan.animeshka.entity.*
import com.arslan.animeshka.repository.UserRepository
import com.arslan.animeshka.service.StudioService
import com.arslan.animeshka.service.UserService
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.flipkart.zjsonpatch.DiffFlags
import com.flipkart.zjsonpatch.JsonDiff
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.toKotlinLocalDate
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.LocalDate
import java.util.*

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
}
