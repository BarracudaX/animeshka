package com.arslan.animeshka

import com.arslan.animeshka.elastic.AnimeDocument
import com.arslan.animeshka.elastic.NovelDocument
import com.arslan.animeshka.entity.User
import com.arslan.animeshka.repository.UserRepository
import com.arslan.animeshka.repository.elastic.AnimeDocumentRepository
import com.arslan.animeshka.repository.elastic.NovelDocumentRepository
import com.arslan.animeshka.service.*
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.toKotlinLocalDate
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import java.time.LocalDate
import kotlin.io.path.Path
import kotlin.io.path.exists

@Profile("dev")
@Component
class ApplicationInitializer(
        private val animeService: AnimeService,
        private val novelService: NovelService,
        private val characterService: CharacterService,
        private val peopleService: PeopleService,
        private val imageService: ImageService,
        private val contentService: ContentService,
        private val studioService: StudioService,
        private val moderationService: ModerationService,
        private val userRepository: UserRepository,
        private val passwordEncoder: PasswordEncoder,
        private val userService: UserService,
        private val animeDocumentRepository: AnimeDocumentRepository,
        private val novelDocumentRepository: NovelDocumentRepository
) : ApplicationRunner{

    override fun run(args: ApplicationArguments) : Unit = runBlocking {
        val user = userRepository.save(User("Test_Anime_Admin","Test_Anime_Admin","AnimeAdmin","anime@admin.com",passwordEncoder.encode("Pass123!"),UserRole.ANIME_ADMINISTRATOR))
        userService.register(UserRegistration("test@email.com","Pass123!","Pass123!","TestUser","test","test"))
        val filePart = AppFilePart(HttpHeaders().apply { add(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=test.jpg") }, Path("./images/test.jpg"))

        val securityContext = ReactiveSecurityContextHolder.withAuthentication(UsernamePasswordAuthenticationToken(user.id,""))
        mono{
            val studio = studioService.createStudio(StudioContent("studio","studio_jp", LocalDate.now().toKotlinLocalDate()))
            imageService.saveImages(Flux.just(filePart,filePart,filePart),filePart,studio)
            moderationService.acceptModeration(studio.id!!)
            studioService.verifyStudio(studio.id)


            val novel1 = novelService.createNovel(NovelContent("test novel 1","test novel 1 jp","test novel 1 synopsis",LocalDate.now().toKotlinLocalDate(),NovelStatus.NOT_YET_PUBLISHED,NovelType.LIGHT_NOVEL,Demographic.JOSEI,"test_bg", themes = setOf(Theme.ADULT_CAST,Theme.BLOOD), genres = setOf(Genre.BOYS_LOVE,Genre.HORROR)))
            val novel2 = novelService.createNovel(NovelContent("another novel world 2","jp another novel world 2","another novel world 2 synopsis",LocalDate.now().toKotlinLocalDate(),NovelStatus.NOT_YET_PUBLISHED,NovelType.LIGHT_NOVEL,Demographic.JOSEI,"test_bg", themes = setOf(Theme.ADULT_CAST,Theme.BLOOD), genres = setOf(Genre.BOYS_LOVE,Genre.HORROR)))
            val novel3 = novelService.createNovel(NovelContent("third novel parallel world 3","jp third novel parallel world 3","third novel parallel world 3 synopsis",LocalDate.now().toKotlinLocalDate(),NovelStatus.NOT_YET_PUBLISHED,NovelType.LIGHT_NOVEL,Demographic.JOSEI,"test_bg", themes = setOf(Theme.ADULT_CAST,Theme.BLOOD), genres = setOf(Genre.BOYS_LOVE,Genre.HORROR)))
            imageService.saveImages(Flux.just(filePart,filePart,filePart),filePart,novel1)
            imageService.saveImages(Flux.just(filePart,filePart,filePart),filePart,novel2)
            imageService.saveImages(Flux.just(filePart,filePart,filePart),filePart,novel3)


            val person = peopleService.createPersonEntry(PersonContent("test","test_ln","test_fn","test_gn","test_desc",LocalDate.now().toKotlinLocalDate()))
            imageService.saveImages(Flux.just(filePart,filePart,filePart),filePart,person)
            val character = characterService.createCharacterEntry(CharacterContent("test","test_jp","test_desc",CharacterRole.ANTAGONIST))
            imageService.saveImages(Flux.just(filePart,filePart,filePart),filePart,character)

            val anime = contentService.createAnimeEntry(AnimeContent("anime title test here in other world","japanese anime title here in other world",AnimeStatus.NOT_YET_AIRED,SeriesRating.G,studio.id!!,Demographic.JOSEI,studio.id,"test_synopsis",AnimeType.OVA,"test_bg","test_add_info",setOf(Theme.BLOOD,Theme.DETECTIVE),setOf(Genre.ACTION,Genre.ADVENTURE), airedAt = LocalDate.now().minusDays(5).toKotlinLocalDate()))
            val anime2 = contentService.createAnimeEntry(AnimeContent("another anime different title here in parallel world","japanese anime different title here in parallel world",AnimeStatus.AIRING,SeriesRating.R_15,studio.id!!,Demographic.SHOUJO,studio.id,"test_synopsis_2",AnimeType.TV,"test_bg_2","test_add_info",setOf(Theme.BLOOD,Theme.DETECTIVE),setOf(Genre.ACTION,Genre.ADVENTURE), airedAt = LocalDate.now().minusDays(5).toKotlinLocalDate()))
            val anime3 = contentService.createAnimeEntry(AnimeContent("anime sword art online","japanese anime sword art online",AnimeStatus.AIRING,SeriesRating.PG_12,studio.id!!,Demographic.SEINEN,studio.id,"test_synopsis_3",AnimeType.SPECIAL,"test_bg_3","test_add_info",setOf(Theme.BLOOD,Theme.DETECTIVE),setOf(Genre.ACTION,Genre.ADVENTURE), airedAt = LocalDate.now().minusDays(5).toKotlinLocalDate()))
            imageService.saveImages(Flux.just(filePart,filePart,filePart),filePart,anime)
            imageService.saveImages(Flux.just(filePart,filePart,filePart),filePart,anime2)
            imageService.saveImages(Flux.just(filePart,filePart,filePart),filePart,anime3)

            moderationService.acceptModeration(person.id!!)
            moderationService.acceptModeration(novel1.id!!)
            moderationService.acceptModeration(novel2.id!!)
            moderationService.acceptModeration(novel3.id!!)
            moderationService.acceptModeration(anime.id!!)
            moderationService.acceptModeration(anime2.id!!)
            moderationService.acceptModeration(anime3.id!!)
            moderationService.acceptModeration(character.id!!)
            characterService.verifyCharacter(character.id)
            val savedNovel1 = novelService.verifyNovel(novel1.id)
            val savedNovel2 = novelService.verifyNovel(novel2.id)
            val savedNovel3 = novelService.verifyNovel(novel3.id)
            val savedAnime = animeService.insertAnime(contentService.verifyAnime(anime.id))
            val savedAnime2 = animeService.insertAnime(contentService.verifyAnime(anime2.id))
            val savedAnime3 = animeService.insertAnime(contentService.verifyAnime(anime3.id))

            animeDocumentRepository.save(AnimeDocument(savedAnime.title,savedAnime.japaneseTitle,savedAnime.synopsis,savedAnime.id)).awaitSingleOrNull()
            animeDocumentRepository.save(AnimeDocument(savedAnime2.title,savedAnime2.japaneseTitle,savedAnime.synopsis,savedAnime2.id)).awaitSingleOrNull()
            animeDocumentRepository.save(AnimeDocument(savedAnime3.title,savedAnime3.japaneseTitle,savedAnime.synopsis,savedAnime3.id)).awaitSingleOrNull()
            novelDocumentRepository.save(NovelDocument(savedNovel1.title,savedNovel1.japaneseTitle,savedNovel1.synopsis,savedNovel1.id)).awaitSingleOrNull()
            novelDocumentRepository.save(NovelDocument(savedNovel2.title,savedNovel2.japaneseTitle,savedNovel2.synopsis,savedNovel2.id)).awaitSingleOrNull()
            novelDocumentRepository.save(NovelDocument(savedNovel3.title,savedNovel3.japaneseTitle,savedNovel3.synopsis,savedNovel3.id)).awaitSingleOrNull()
            peopleService.verifyPerson(person.id)
        }.contextWrite(securityContext).awaitSingleOrNull()

    }

}