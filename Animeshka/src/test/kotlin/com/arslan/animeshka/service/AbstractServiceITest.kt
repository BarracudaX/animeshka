package com.arslan.animeshka.service

import com.arslan.animeshka.*
import com.arslan.animeshka.entity.*
import com.arslan.animeshka.repository.*
import com.arslan.animeshka.repository.elastic.NovelDocumentRepository
import com.arslan.animeshka.repository.elastic.PeopleDocumentRepository
import com.arslan.animeshka.repository.elastic.StudioDocumentRepository
import kotlinx.coroutines.flow.toSet
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.MessageSource
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate
import org.springframework.data.elasticsearch.core.RefreshPolicy
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.containers.MySQLR2DBCDatabaseContainer
import org.testcontainers.elasticsearch.ElasticsearchContainer
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.time.LocalDate
import java.util.*

@SpringBootTest
@Testcontainers
abstract class AbstractServiceITest : AbstractTest(){

    @Autowired
    protected lateinit var json: Json

    @Autowired
    protected lateinit var transactionalOperator: TransactionalOperator

    @Autowired
    protected lateinit var messageSource: MessageSource

    @Autowired
    protected lateinit var moderationService: ModerationService

    @Autowired
    protected lateinit var userRepository: UserRepository

    @Autowired
    protected lateinit var userRoleRepository: UserRoleRepository

    @Autowired
    protected lateinit var studioRepository: StudioRepository

    @Autowired
    protected lateinit var contentRepository: ContentRepository

    @Autowired
    protected lateinit var studioDocumentRepository: StudioDocumentRepository

    @Autowired
    protected lateinit var elasticsearchTemplate: ReactiveElasticsearchTemplate

    @Autowired
    protected lateinit var peopleRepository: PeopleRepository

    @Autowired
    protected lateinit var peopleDocumentRepository: PeopleDocumentRepository

    @Autowired
    protected lateinit var characterRepository: CharacterRepository

    @Autowired
    protected lateinit var animeRepository: AnimeRepository

    @Autowired
    protected lateinit var novelRepository: NovelRepository

    @Autowired
    protected lateinit var databaseClient: DatabaseClient

    @Autowired
    protected lateinit var novelDocumentRepository: NovelDocumentRepository

    @Autowired
    protected lateinit var magazineRepository: MagazineRepository

    @BeforeEach
    fun prepare(){
        elasticsearchTemplate.refreshPolicy = RefreshPolicy.IMMEDIATE
    }

    protected suspend fun Novel.novelRelations() : Set<WorkRelation> = databaseClient
            .sql("SELECT * FROM NOVEL_NOVEL_RELATIONS WHERE novel_id = :id")
            .bind("id",id)
            .map { row,_ -> WorkRelation(row.get("related_novel_id",Long::class.java)!!,Relation.valueOf(row.get("relation",String::class.java)!!))}
            .all().asFlow().toSet()

    protected suspend fun Novel.animeRelations() : Set<WorkRelation> = databaseClient
            .sql("SELECT * FROM NOVEL_ANIME_RELATIONS WHERE novel_id = :id")
            .bind("id",id)
            .map { row,_ -> WorkRelation(row.get("anime_id",Long::class.java)!!,Relation.valueOf(row.get("relation",String::class.java)!!)) }
            .all().asFlow().toSet()

    protected suspend fun Novel.characters() : Set<NovelCharacter> = databaseClient
            .sql("SELECT * FROM NOVEL_CHARACTERS WHERE novel_id = :id")
            .bind("id",id)
            .map { row,_ -> NovelCharacter(row.get("character_id",Long::class.java)!!,CharacterRole.valueOf(row.get("character_role",String::class.java)!!)) }
            .all().asFlow().toSet()

    protected suspend fun Novel.themes() : Set<Theme> = databaseClient
            .sql("SELECT * FROM NOVEL_THEMES WHERE novel_id = :id")
            .bind("id",id)
            .map { row, _ -> Theme.valueOf(row.get("theme",String::class.java)!!) }
            .all().asFlow().toSet()

    protected suspend fun Novel.genres() : Set<Genre> = databaseClient
            .sql("SELECT * FROM NOVEL_GENRES WHERE novel_id = :id")
            .bind("id", id)
            .map { row, _ -> Genre.valueOf(row.get("genre",String::class.java)!!) }
            .all().asFlow().toSet()
    protected fun randomRelation() : Relation = Relation.values().random()

    protected fun randomCharacterRole() : CharacterRole = CharacterRole.values().random()

    protected suspend fun createNovel() : Novel{
        val creatorID = createPlainUser().id!!
        val contentID = contentRepository.save(Content(creatorID,ContentType.NOVEL,"{}",UUID.randomUUID().toString(),ContentStatus.VERIFIED)).id!!
        return novelRepository.save(Novel(UUID.randomUUID().toString(),UUID.randomUUID().toString(),UUID.randomUUID().toString(),LocalDate.now(),NovelStatus.FINISHED,NovelType.NOVEL,Demographic.SEINEN,id = contentID).apply { isNewEntity = true })
    }

    protected suspend fun createAnime() : Anime{
        val creatorID = createPlainUser().id!!
        val contentID = contentRepository.save(Content(creatorID,ContentType.ANIME,"{}",UUID.randomUUID().toString(),ContentStatus.VERIFIED)).id!!
        return animeRepository.save(Anime(UUID.randomUUID().toString(),AnimeStatus.AIRING,SeriesRating.PG_12,createStudio().id,Demographic.SEINEN,createStudio().id,UUID.randomUUID().toString(),UUID.randomUUID().toString(),id = contentID).apply { isNewEntity = true })
    }

    protected suspend fun createPerson() : Person{
        val creatorID = createPlainUser().id!!
        val contentID = contentRepository.save(Content(creatorID,ContentType.PERSON,"{}",UUID.randomUUID().toString(),ContentStatus.VERIFIED)).id!!
        return peopleRepository.save(Person(UUID.randomUUID().toString(),UUID.randomUUID().toString(),id = contentID).apply { isNewEntity = true })
    }
    protected suspend fun createStudio() : Studio{
        val creatorID = createPlainUser().id!!
        val contentID = contentRepository.save(Content(creatorID,ContentType.STUDIO,"{}",UUID.randomUUID().toString(),ContentStatus.VERIFIED)).id!!
        return studioRepository.save(Studio(UUID.randomUUID().toString(),UUID.randomUUID().toString(), LocalDate.now(),contentID).apply { isNewEntity = true })
    }

    protected suspend fun createCharacter() : Character{
        val creatorID = createPlainUser().id!!
        val contentID = contentRepository.save(Content(creatorID,ContentType.CHARACTER,"{}",UUID.randomUUID().toString(),ContentStatus.VERIFIED)).id!!
        return characterRepository.save(Character(UUID.randomUUID().toString(),UUID.randomUUID().toString(),UUID.randomUUID().toString(),contentID).apply { isNewEntity = true })
    }

    protected fun runTransactionalTest(block: suspend () -> Unit) = runTest{
        transactionalOperator.executeAndAwait {
            block()
            it.setRollbackOnly()
        }
    }

    protected fun runTransactionalTestWithAnimeAdminUser(block: suspend (user: User) -> Unit) = runTest {
        transactionalOperator.executeAndAwait {
            val admin = createAdminUser()
            val context = ReactiveSecurityContextHolder.withAuthentication(UsernamePasswordAuthenticationToken(admin.id,""))
            mono{
                block(admin)
                it.setRollbackOnly()
            }.contextWrite(context).awaitSingleOrNull()
        }
    }

    protected suspend fun createAdminUser(): User {
        val user = userRepository.save(User("admin_fn","admin_ln", UUID.randomUUID().toString(),UUID.randomUUID().toString(),"Admin123!"))
        userRoleRepository.save(UserRole(user.id!!,Role.ANIME_ADMINISTRATOR))
        return user
    }

    protected suspend fun createPlainUser() : User{
        val user = userRepository.save(User("user_fn","user_ln",UUID.randomUUID().toString(),UUID.randomUUID().toString(),"User123!"))
        userRoleRepository.save(UserRole(user.id!!,Role.USER))
        return user
    }


    companion object{

        private val mysqlContainer = MySQLContainer("mysql:latest")
            .withReuse(true)
            .withDatabaseName("animeshka")
            .withUsername("root")
            .withPassword("root")
        private val r2dbcContainer = MySQLR2DBCDatabaseContainer(mysqlContainer)
        private val elasticImage = DockerImageName.parse("elasticsearch:8.7.0").asCompatibleSubstituteFor("docker.elastic.co/elasticsearch/elasticsearch")
        private val elasticSearchContainer = ElasticsearchContainer(elasticImage)

        @JvmStatic
        @DynamicPropertySource
        fun registerPropertySource(registry: DynamicPropertyRegistry){
            r2dbcContainer.start()
            elasticSearchContainer.apply {
                addEnv("xpack.security.enabled","false")
            }.start()
            registry.add("spring.r2dbc.url"){ "r2dbc:mysql://${mysqlContainer.host}:${mysqlContainer.firstMappedPort}/animeshka" }
            registry.add("spring.elasticsearch.uris"){ "http://${elasticSearchContainer.host}:${elasticSearchContainer.firstMappedPort}" }
        }

    }

}