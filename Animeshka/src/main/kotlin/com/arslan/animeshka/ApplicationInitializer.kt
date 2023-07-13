package com.arslan.animeshka

import com.arslan.animeshka.elastic.*
import com.arslan.animeshka.entity.User
import com.arslan.animeshka.repository.UserRepository
import com.arslan.animeshka.repository.elastic.*
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
        private val novelDocumentRepository: NovelDocumentRepository,
        private val characterDocumentRepository: CharacterDocumentRepository,
        private val peopleDocumentRepository: PeopleDocumentRepository,
        private val studioDocumentRepository: StudioDocumentRepository
) : ApplicationRunner{

    override fun run(args: ApplicationArguments) : Unit = runBlocking {
        val user = userRepository.save(User("Test_Anime_Admin","Test_Anime_Admin","AnimeAdmin","anime@admin.com",passwordEncoder.encode("Pass123!")))
        userService.register(UserRegistration("test@email.com","Pass123!","Pass123!","TestUser","test","test"))
        val filePart = AppFilePart(HttpHeaders().apply { add(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=test.jpg") }, Path("./images/test.jpg"))

        val securityContext = ReactiveSecurityContextHolder.withAuthentication(UsernamePasswordAuthenticationToken(user.id,""))
        mono{
            val studio1 = contentService.createStudioEntry(StudioContent("studio","studio_jp", LocalDate.now().toKotlinLocalDate()))
            val studio2 = contentService.createStudioEntry(StudioContent("another studio from japan","makisama studio", LocalDate.now().toKotlinLocalDate()))
            val studio3 = contentService.createStudioEntry(StudioContent("third studio japan","origami paralax", LocalDate.now().toKotlinLocalDate()))
            imageService.saveImages(Flux.just(filePart,filePart,filePart),filePart,studio1)
            imageService.saveImages(Flux.just(filePart,filePart,filePart),filePart,studio2)
            imageService.saveImages(Flux.just(filePart,filePart,filePart),filePart,studio3)
            moderationService.acceptModeration(studio1.id!!)
            moderationService.acceptModeration(studio2.id!!)
            moderationService.acceptModeration(studio3.id!!)

            val savedStudio1 = studioService.insertStudio(contentService.verifyStudio(studio1.id))
            val savedStudio2 = studioService.insertStudio(contentService.verifyStudio(studio2.id))
            val savedStudio3 = studioService.insertStudio(contentService.verifyStudio(studio3.id))

            val novel1 = contentService.createNovelEntry(NovelContent("test novel 1","test novel 1 jp","test novel 1 synopsis",LocalDate.now().toKotlinLocalDate(),NovelStatus.NOT_YET_PUBLISHED,NovelType.LIGHT_NOVEL,Demographic.JOSEI,"test_bg", themes = setOf(Theme.ADULT_CAST,Theme.BLOOD), genres = setOf(Genre.BOYS_LOVE,Genre.HORROR)))
            val novel2 = contentService.createNovelEntry(NovelContent("another novel world 2","jp another novel world 2","another novel world 2 synopsis",LocalDate.now().toKotlinLocalDate(),NovelStatus.NOT_YET_PUBLISHED,NovelType.LIGHT_NOVEL,Demographic.JOSEI,"test_bg", themes = setOf(Theme.ADULT_CAST,Theme.BLOOD), genres = setOf(Genre.BOYS_LOVE,Genre.HORROR)))
            val novel3 = contentService.createNovelEntry(NovelContent("third novel parallel world 3","jp third novel parallel world 3","third novel parallel world 3 synopsis",LocalDate.now().toKotlinLocalDate(),NovelStatus.NOT_YET_PUBLISHED,NovelType.LIGHT_NOVEL,Demographic.JOSEI,"test_bg", themes = setOf(Theme.ADULT_CAST,Theme.BLOOD), genres = setOf(Genre.BOYS_LOVE,Genre.HORROR)))
            imageService.saveImages(Flux.just(filePart,filePart,filePart),filePart,novel1)
            imageService.saveImages(Flux.just(filePart,filePart,filePart),filePart,novel2)
            imageService.saveImages(Flux.just(filePart,filePart,filePart),filePart,novel3)


            val person1 = contentService.createPersonEntry(PersonContent("one person test","one person test2","one person family","one person given","test_desc",LocalDate.now().toKotlinLocalDate()))
            val person2 = contentService.createPersonEntry(PersonContent("two person first","two person last","two person semiya","two person some","test_desc",LocalDate.now().toKotlinLocalDate()))
            val person3 = contentService.createPersonEntry(PersonContent("third person first","third person familiya",null,null,"test_desc",LocalDate.now().toKotlinLocalDate()))
            imageService.saveImages(Flux.empty(),filePart,person1)
            imageService.saveImages(Flux.empty(),filePart,person2)
            imageService.saveImages(Flux.empty(),filePart,person3)

            val character1 = contentService.createCharacterEntry(CharacterContent("test character","jp test character","test_desc"))
            val character2 = contentService.createCharacterEntry(CharacterContent("another character Dio","another character Dio jp","test_desc_2"))
            val character3 = contentService.createCharacterEntry(CharacterContent("third another character Rio","third character Rio jp","test_desc_3"))
            imageService.saveImages(Flux.just(filePart,filePart,filePart),filePart,character1)
            imageService.saveImages(Flux.just(filePart,filePart,filePart),filePart,character2)
            imageService.saveImages(Flux.just(filePart,filePart,filePart),filePart,character3)

            val anime = contentService.createAnimeEntry(AnimeContent("anime title test here in other world","japanese anime title here in other world",AnimeStatus.NOT_YET_AIRED,SeriesRating.G,studio1.id!!,Demographic.JOSEI,studio1.id,"test_synopsis",AnimeType.OVA,"test_bg","test_add_info",setOf(Theme.BLOOD,Theme.DETECTIVE),setOf(Genre.ACTION,Genre.ADVENTURE), airedAt = LocalDate.now().minusDays(5).toKotlinLocalDate()))
            val anime2 = contentService.createAnimeEntry(AnimeContent("another anime different title here in parallel world","japanese anime different title here in parallel world",AnimeStatus.AIRING,SeriesRating.R_15,studio1.id!!,Demographic.SHOUJO,studio1.id,"test_synopsis_2",AnimeType.TV,"test_bg_2","test_add_info",setOf(Theme.BLOOD,Theme.DETECTIVE),setOf(Genre.ACTION,Genre.ADVENTURE), airedAt = LocalDate.now().minusDays(5).toKotlinLocalDate()))
            val anime3 = contentService.createAnimeEntry(AnimeContent("anime sword art online","japanese anime sword art online",AnimeStatus.AIRING,SeriesRating.PG_12,studio1.id!!,Demographic.SEINEN,studio1.id,"test_synopsis_3",AnimeType.SPECIAL,"test_bg_3","test_add_info",setOf(Theme.BLOOD,Theme.DETECTIVE),setOf(Genre.ACTION,Genre.ADVENTURE), airedAt = LocalDate.now().minusDays(5).toKotlinLocalDate()))
            imageService.saveImages(Flux.just(filePart,filePart,filePart),filePart,anime)
            imageService.saveImages(Flux.just(filePart,filePart,filePart),filePart,anime2)
            imageService.saveImages(Flux.just(filePart,filePart,filePart),filePart,anime3)

            moderationService.acceptModeration(person1.id!!)
            moderationService.acceptModeration(person2.id!!)
            moderationService.acceptModeration(person3.id!!)
            moderationService.acceptModeration(novel1.id!!)
            moderationService.acceptModeration(novel2.id!!)
            moderationService.acceptModeration(novel3.id!!)
            moderationService.acceptModeration(anime.id!!)
            moderationService.acceptModeration(anime2.id!!)
            moderationService.acceptModeration(anime3.id!!)
            moderationService.acceptModeration(character1.id!!)
            moderationService.acceptModeration(character2.id!!)
            moderationService.acceptModeration(character3.id!!)
            val savedCharacter1 = characterService.insertCharacter(contentService.verifyCharacter(character1.id))
            val savedCharacter2 = characterService.insertCharacter(contentService.verifyCharacter(character2.id))
            val savedCharacter3 = characterService.insertCharacter(contentService.verifyCharacter(character3.id))
            val savedNovel1 = novelService.insertNovel(contentService.verifyNovel(novel1.id))
            val savedNovel2 = novelService.insertNovel(contentService.verifyNovel(novel2.id))
            val savedNovel3 = novelService.insertNovel(contentService.verifyNovel(novel3.id))
            val savedAnime = animeService.insertAnime(contentService.verifyAnime(anime.id))
            val savedAnime2 = animeService.insertAnime(contentService.verifyAnime(anime2.id))
            val savedAnime3 = animeService.insertAnime(contentService.verifyAnime(anime3.id))

            val savedPerson1 = peopleService.insertPerson(contentService.verifyPerson(person1.id))
            val savedPerson2 = peopleService.insertPerson(contentService.verifyPerson(person2.id))
            val savedPerson3 = peopleService.insertPerson(contentService.verifyPerson(person3.id))

            animeDocumentRepository.save(AnimeDocument(savedAnime.title,savedAnime.japaneseTitle,savedAnime.synopsis,savedAnime.id)).awaitSingleOrNull()
            animeDocumentRepository.save(AnimeDocument(savedAnime2.title,savedAnime2.japaneseTitle,savedAnime.synopsis,savedAnime2.id)).awaitSingleOrNull()
            animeDocumentRepository.save(AnimeDocument(savedAnime3.title,savedAnime3.japaneseTitle,savedAnime.synopsis,savedAnime3.id)).awaitSingleOrNull()
            novelDocumentRepository.save(NovelDocument(savedNovel1.title,savedNovel1.japaneseTitle,savedNovel1.synopsis,savedNovel1.id)).awaitSingleOrNull()
            novelDocumentRepository.save(NovelDocument(savedNovel2.title,savedNovel2.japaneseTitle,savedNovel2.synopsis,savedNovel2.id)).awaitSingleOrNull()
            novelDocumentRepository.save(NovelDocument(savedNovel3.title,savedNovel3.japaneseTitle,savedNovel3.synopsis,savedNovel3.id)).awaitSingleOrNull()
            characterDocumentRepository.save(CharacterDocument(savedCharacter1.characterName,savedCharacter1.description,savedCharacter1.japaneseName,savedCharacter1.id)).awaitSingleOrNull()
            characterDocumentRepository.save(CharacterDocument(savedCharacter2.characterName,savedCharacter2.description,savedCharacter2.japaneseName,savedCharacter2.id)).awaitSingleOrNull()
            characterDocumentRepository.save(CharacterDocument(savedCharacter3.characterName,savedCharacter3.description,savedCharacter3.japaneseName,savedCharacter3.id)).awaitSingleOrNull()
            peopleDocumentRepository.save(PersonDocument(savedPerson1.firstName,savedPerson1.lastName,savedPerson1.familyName,savedPerson1.givenName,savedPerson1.description,savedPerson1.id)).awaitSingleOrNull()
            peopleDocumentRepository.save(PersonDocument(savedPerson2.firstName,savedPerson2.lastName,savedPerson2.familyName,savedPerson2.givenName,savedPerson2.description,savedPerson2.id)).awaitSingleOrNull()
            peopleDocumentRepository.save(PersonDocument(savedPerson3.firstName,savedPerson3.lastName,savedPerson3.familyName,savedPerson3.givenName,savedPerson3.description,savedPerson3.id)).awaitSingleOrNull()
            studioDocumentRepository.save(StudioDocument(savedStudio1.studioName,savedStudio1.japaneseName,savedStudio1.id)).awaitSingleOrNull()
            studioDocumentRepository.save(StudioDocument(savedStudio2.studioName,savedStudio2.japaneseName,savedStudio2.id)).awaitSingleOrNull()
            studioDocumentRepository.save(StudioDocument(savedStudio3.studioName,savedStudio3.japaneseName,savedStudio3.id)).awaitSingleOrNull()
        }.contextWrite(securityContext).awaitSingleOrNull()

    }

}