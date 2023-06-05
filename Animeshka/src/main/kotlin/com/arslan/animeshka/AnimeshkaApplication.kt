package com.arslan.animeshka

import com.arslan.animeshka.entity.User
import com.arslan.animeshka.repository.UserRepository
import com.arslan.animeshka.service.*
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactor.mono
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.toKotlinLocalDate
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.LocalDate
import kotlin.io.path.Path

@SpringBootApplication
class AnimeshkaApplication

fun main(args: Array<String>) : Unit = runBlocking{
    val context = runApplication<AnimeshkaApplication>(*args)
    val moderationService = context.getBean(ModerationService::class.java)
    val userService = context.getBean(UserService::class.java)
    val userRepository = context.getBean(UserRepository::class.java)
    val passwordEncoder = context.getBean(PasswordEncoder::class.java)
    val studioService = context.getBean(StudioService::class.java)
    val novelService = context.getBean(NovelService::class.java)
    val animeService = context.getBean(AnimeService::class.java)
    val characterService = context.getBean(CharacterService::class.java)
    val peopleService = context.getBean(PeopleService::class.java)
    val user = userRepository.save(User("Test_Anime_Admin","Test_Anime_Admin","AnimeAdmin","anime@admin.com",passwordEncoder.encode("Pass123!"),UserRole.ANIME_ADMINISTRATOR))
    userService.register(UserRegistration("test@email.com","Pass123!","Pass123!","TestUser","test","test"))

    val securityContext = ReactiveSecurityContextHolder.withAuthentication(UsernamePasswordAuthenticationToken(user.id,""))
    mono {
        val headers = HttpHeaders().apply { add(HttpHeaders.CONTENT_DISPOSITION,"image;filename=image.jpg") }
        val testPoster = AppFilePart(headers, Path("/animeshka/test.jpg"))
        val studio = studioService.createStudio(StudioContent("studio","studio_jp",LocalDate.now().toKotlinLocalDate()))
        val novel = novelService.createNovel(
            NovelContent("test","test_jp","test_syn",LocalDate.now().toKotlinLocalDate(),NovelStatus.NOT_YET_PUBLISHED,NovelType.LIGHT_NOVEL,Demographic.JOSEI,"test_bg", themes = setOf(Theme.ADULT_CAST,Theme.BLOOD), genres = setOf(Genre.BOYS_LOVE,Genre.HORROR)),
            testPoster
        )
        val person = peopleService.createPersonEntry(PersonContent("test","test_ln","test_fn","test_gn","test_desc",LocalDate.now().toKotlinLocalDate()),testPoster)
        val character = characterService.createCharacterEntry(CharacterContent("test","test_jp","test_desc",CharacterRole.ANTAGONIST),testPoster)
        val anime = animeService.createUnverifiedAnime(
            AnimeContent("test","test_jp",AnimeStatus.NOT_YET_AIRED,SeriesRating.G,studio.id!!,Demographic.JOSEI,studio.id,"test_synopsis",AnimeType.OVA,"test_bg","test_add_info",setOf(Theme.BLOOD,Theme.DETECTIVE),setOf(Genre.ACTION,Genre.ADVENTURE), airedAt = LocalDate.now().minusDays(5).toKotlinLocalDate()),
            testPoster
        )
        moderationService.acceptModeration(person.id!!)
        moderationService.acceptModeration(novel.id!!)
        moderationService.acceptModeration(studio.id)
        moderationService.acceptModeration(anime.id!!)
        moderationService.acceptModeration(character.id!!)
        characterService.verifyCharacter(character.id)
        studioService.verifyStudio(studio.id)
        novelService.verifyNovel(novel.id)
        animeService.verifyAnimeEntry(anime.id)
        peopleService.verifyPerson(person.id)
    }.contextWrite(securityContext).awaitFirst()


}
