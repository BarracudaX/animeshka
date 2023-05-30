package com.arslan.animeshka

import com.arslan.animeshka.entity.User
import com.arslan.animeshka.repository.UserRepository
import com.arslan.animeshka.service.UserService
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.flipkart.zjsonpatch.DiffFlags
import com.flipkart.zjsonpatch.JsonDiff
import com.flipkart.zjsonpatch.JsonPatch
import kotlinx.coroutines.runBlocking
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*

@SpringBootApplication
class AnimeshkaApplication

fun main(args: Array<String>) : Unit = runBlocking{
    val context = runApplication<AnimeshkaApplication>(*args)
    val userService = context.getBean(UserService::class.java)
    val userRepository = context.getBean(UserRepository::class.java)
    val passwordEncoder = context.getBean(PasswordEncoder::class.java)
    userRepository.save(User("Test_Anime_Admin","Test_Anime_Admin","AnimeAdmin","anime@admin.com",passwordEncoder.encode("Pass123!"),UserRole.ANIME_ADMINISTRATOR))
    userService.register(UserRegistration("test@email.com","Pass123!","Pass123!","TestUser","test","test"))

//    val anime = AnimeDTO("test","test",
//        AnimeStatus.AIRING,
//        SeriesRating.G,1,
//        Demographic.SHOUNEN,1,"test",
//        AnimeType.TV,"test","test",setOf(Theme.DETECTIVE, Theme.CHILDCARE, Theme.ADULT_CAST, Theme.COMBAT_SPORTS,Theme.ART),id = 1)
//    val anime2 = AnimeDTO("test","test",
//        AnimeStatus.AIRING,
//        SeriesRating.G,1,
//        Demographic.SHOUNEN,1,"test",
//        AnimeType.TV,"test","test",setOf(Theme.CHILDCARE, Theme.COMBAT_SPORTS,Theme.ANTHROPOMORPHIC,Theme.ADULT_CAST,Theme.BLOOD),id = 1)
//    val objectMapper = ObjectMapper()
//    val animeNode = objectMapper.convertValue(anime,JsonNode::class.java)
//    val animeNode2 = objectMapper.convertValue(anime2,JsonNode::class.java)
//
//    val diff = JsonDiff.asJson(animeNode,animeNode2, EnumSet.of(DiffFlags.ADD_ORIGINAL_VALUE_ON_REPLACE,DiffFlags.ADD_EXPLICIT_REMOVE_ADD_ON_REPLACE))
//    println("Before Removal : $diff")
//    (diff as ArrayNode).remove(1)
//    println("After Removal : $diff")
//    for((index,change) in diff.withIndex()){
//        println(change)
//    }

}
