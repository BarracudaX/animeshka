package com.arslan.animeshka

import com.arslan.animeshka.entity.User
import com.arslan.animeshka.repository.UserRepository
import com.arslan.animeshka.service.ContentService
import com.arslan.animeshka.service.ModerationService
import com.arslan.animeshka.service.NovelService
import com.arslan.animeshka.service.UserService
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.flipkart.zjsonpatch.DiffFlags
import com.flipkart.zjsonpatch.JsonDiff
import com.flipkart.zjsonpatch.JsonPatch
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactor.mono
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.toKotlinLocalDate
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import reactor.core.publisher.Mono
import java.time.LocalDate
import java.util.*

@SpringBootApplication
class AnimeshkaApplication

fun main(args: Array<String>) : Unit = runBlocking{
    val context = runApplication<AnimeshkaApplication>(*args)
    val contentService = context.getBean(ContentService::class.java)
    val moderationService = context.getBean(ModerationService::class.java)
    val userService = context.getBean(UserService::class.java)
    val userRepository = context.getBean(UserRepository::class.java)
    val passwordEncoder = context.getBean(PasswordEncoder::class.java)
    val novelService = context.getBean(NovelService::class.java)
    val user = userRepository.save(User("Test_Anime_Admin","Test_Anime_Admin","AnimeAdmin","anime@admin.com",passwordEncoder.encode("Pass123!"),UserRole.ANIME_ADMINISTRATOR))
    userService.register(UserRegistration("test@email.com","Pass123!","Pass123!","TestUser","test","test"))

    val securityContext = ReactiveSecurityContextHolder.withAuthentication(UsernamePasswordAuthenticationToken(user.id,""))

    mono {
        val content = novelService.createNovel(UnverifiedNovel("test","test_jp","test_syn",LocalDate.now().toKotlinLocalDate(),NovelStatus.NOT_YET_PUBLISHED,NovelType.LIGHT_NOVEL,Demographic.JOSEI,"test_bg", themes = setOf(Theme.ADULT_CAST,Theme.BLOOD), genres = setOf(Genre.BOYS_LOVE,Genre.HORROR)))
        moderationService.acceptModeration(content.id!!)
        novelService.verifyNovel(content.id)
    }.contextWrite(securityContext).awaitFirst()


}
