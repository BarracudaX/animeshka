package com.arslan.animeshka.service

import com.arslan.animeshka.AbstractTest
import com.arslan.animeshka.Role
import com.arslan.animeshka.entity.User
import com.arslan.animeshka.entity.UserRole
import com.arslan.animeshka.repository.ContentRepository
import com.arslan.animeshka.repository.StudioRepository
import com.arslan.animeshka.repository.UserRepository
import com.arslan.animeshka.repository.UserRoleRepository
import com.arslan.animeshka.repository.elastic.StudioDocumentRepository
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.MessageSource
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate
import org.springframework.data.elasticsearch.core.RefreshPolicy
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

    @BeforeEach
    fun prepare(){
        elasticsearchTemplate.refreshPolicy = RefreshPolicy.IMMEDIATE
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
        val user = userRepository.save(User("admin_fn","admin_ln","admin_usn","admin@admin.com","Admin123!"))
        userRoleRepository.save(UserRole(user.id!!,Role.ANIME_ADMINISTRATOR))
        return user
    }

    protected suspend fun createPlainUser() : User{
        val user = userRepository.save(User("user_fn","user_ln","user_usn","user@user.com","User123!"))
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