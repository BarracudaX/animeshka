package com.arslan.animeshka

import com.arslan.animeshka.elastic.AnimeDocument
import com.arslan.animeshka.elastic.NovelDocument
import com.arslan.animeshka.entity.User
import com.arslan.animeshka.repository.UserRepository
import com.arslan.animeshka.repository.elastic.AnimeDocumentRepository
import com.arslan.animeshka.repository.elastic.NovelDocumentRepository
import com.arslan.animeshka.service.*
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactor.awaitSingleOrNull
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
    val animeDocumentRepository = context.getBean(AnimeDocumentRepository::class.java)
    val novelDocumentRepository = context.getBean(NovelDocumentRepository::class.java)
    val user = userRepository.save(User("Test_Anime_Admin","Test_Anime_Admin","AnimeAdmin","anime@admin.com",passwordEncoder.encode("Pass123!"),UserRole.ANIME_ADMINISTRATOR))
    userService.register(UserRegistration("test@email.com","Pass123!","Pass123!","TestUser","test","test"))

    val securityContext = ReactiveSecurityContextHolder.withAuthentication(UsernamePasswordAuthenticationToken(user.id,""))
    mono {
        val headers = HttpHeaders().apply { add(HttpHeaders.CONTENT_DISPOSITION,"image;filename=image.jpg") }
        val testPoster = AppFilePart(headers, Path("./images/test.jpg"))
        val studio = studioService.createStudio(StudioContent("studio","studio_jp",LocalDate.now().toKotlinLocalDate()))
        val novel1 = novelService.createNovel(
            NovelContent("test novel 1","test novel 1 jp","test novel 1 synopsis",LocalDate.now().toKotlinLocalDate(),NovelStatus.NOT_YET_PUBLISHED,NovelType.LIGHT_NOVEL,Demographic.JOSEI,"test_bg", themes = setOf(Theme.ADULT_CAST,Theme.BLOOD), genres = setOf(Genre.BOYS_LOVE,Genre.HORROR)),
            testPoster
        )
        val novel2 = novelService.createNovel(
                NovelContent("another novel world 2","jp another novel world 2","another novel world 2 synopsis",LocalDate.now().toKotlinLocalDate(),NovelStatus.NOT_YET_PUBLISHED,NovelType.LIGHT_NOVEL,Demographic.JOSEI,"test_bg", themes = setOf(Theme.ADULT_CAST,Theme.BLOOD), genres = setOf(Genre.BOYS_LOVE,Genre.HORROR)),
                testPoster
        )
        val novel3 = novelService.createNovel(
                NovelContent("third novel parallel world 3","jp third novel parallel world 3","third novel parallel world 3 synopsis",LocalDate.now().toKotlinLocalDate(),NovelStatus.NOT_YET_PUBLISHED,NovelType.LIGHT_NOVEL,Demographic.JOSEI,"test_bg", themes = setOf(Theme.ADULT_CAST,Theme.BLOOD), genres = setOf(Genre.BOYS_LOVE,Genre.HORROR)),
                testPoster
        )
        val person = peopleService.createPersonEntry(PersonContent("test","test_ln","test_fn","test_gn","test_desc",LocalDate.now().toKotlinLocalDate()),testPoster)
        val character = characterService.createCharacterEntry(CharacterContent("test","test_jp","test_desc",CharacterRole.ANTAGONIST),testPoster)
        val anime = animeService.createUnverifiedAnime(
            AnimeContent("anime title test here in other world","japanese anime title here in other world",AnimeStatus.NOT_YET_AIRED,SeriesRating.G,studio.id!!,Demographic.JOSEI,studio.id,"test_synopsis",AnimeType.OVA,"test_bg","test_add_info",setOf(Theme.BLOOD,Theme.DETECTIVE),setOf(Genre.ACTION,Genre.ADVENTURE), airedAt = LocalDate.now().minusDays(5).toKotlinLocalDate()),
            testPoster
        )
        val anime2 = animeService.createUnverifiedAnime(
                AnimeContent("another anime different title here in parallel world","japanese anime different title here in parallel world",AnimeStatus.AIRING,SeriesRating.R_15,studio.id!!,Demographic.SHOUJO,studio.id,"test_synopsis_2",AnimeType.TV,"test_bg_2","test_add_info",setOf(Theme.BLOOD,Theme.DETECTIVE),setOf(Genre.ACTION,Genre.ADVENTURE), airedAt = LocalDate.now().minusDays(5).toKotlinLocalDate()),
                testPoster
        )

        val anime3 = animeService.createUnverifiedAnime(
                AnimeContent("anime sword art online","japanese anime sword art online",AnimeStatus.AIRING,SeriesRating.PG_12,studio.id!!,Demographic.SEINEN,studio.id,"test_synopsis_3",AnimeType.SPECIAL,"test_bg_3","test_add_info",setOf(Theme.BLOOD,Theme.DETECTIVE),setOf(Genre.ACTION,Genre.ADVENTURE), airedAt = LocalDate.now().minusDays(5).toKotlinLocalDate()),
                testPoster
        )
        moderationService.acceptModeration(person.id!!)
        moderationService.acceptModeration(novel1.id!!)
        moderationService.acceptModeration(novel2.id!!)
        moderationService.acceptModeration(novel3.id!!)
        moderationService.acceptModeration(studio.id)
        moderationService.acceptModeration(anime.id!!)
        moderationService.acceptModeration(anime2.id!!)
        moderationService.acceptModeration(anime3.id!!)
        moderationService.acceptModeration(character.id!!)
        characterService.verifyCharacter(character.id)
        studioService.verifyStudio(studio.id)
        val savedNovel1 = novelService.verifyNovel(novel1.id)
        val savedNovel2 = novelService.verifyNovel(novel2.id)
        val savedNovel3 = novelService.verifyNovel(novel3.id)
        val savedAnime = animeService.verifyAnimeEntry(anime.id)
        val savedAnime2 = animeService.verifyAnimeEntry(anime2.id)
        val savedAnime3 = animeService.verifyAnimeEntry(anime3.id)
        animeDocumentRepository.save(AnimeDocument(savedAnime.title,savedAnime.japaneseTitle,savedAnime.synopsis,savedAnime.id)).awaitSingleOrNull()
        animeDocumentRepository.save(AnimeDocument(savedAnime2.title,savedAnime2.japaneseTitle,savedAnime.synopsis,savedAnime2.id)).awaitSingleOrNull()
        animeDocumentRepository.save(AnimeDocument(savedAnime3.title,savedAnime3.japaneseTitle,savedAnime.synopsis,savedAnime3.id)).awaitSingleOrNull()
        novelDocumentRepository.save(NovelDocument(savedNovel1.title,savedNovel1.japaneseTitle,savedNovel1.synopsis,savedNovel1.id)).awaitSingleOrNull()
        novelDocumentRepository.save(NovelDocument(savedNovel2.title,savedNovel2.japaneseTitle,savedNovel2.synopsis,savedNovel2.id)).awaitSingleOrNull()
        novelDocumentRepository.save(NovelDocument(savedNovel3.title,savedNovel3.japaneseTitle,savedNovel3.synopsis,savedNovel3.id)).awaitSingleOrNull()
        peopleService.verifyPerson(person.id)
    }.contextWrite(securityContext).awaitFirst()


}
