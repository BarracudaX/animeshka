package com.arslan.animeshka.service

import com.arslan.animeshka.AbstractTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.MessageSource
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.containers.MySQLR2DBCDatabaseContainer
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.elasticsearch.ElasticsearchContainer
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@SpringBootTest
@Testcontainers
abstract class AbstractServiceITest : AbstractTest(){


    @Autowired
    protected lateinit var transactionalOperator: TransactionalOperator

    @Autowired
    protected lateinit var messageSource: MessageSource

    protected fun runTransactionalTest(block: suspend () -> Unit) = runTest{
        transactionalOperator.executeAndAwait {
            block()
            it.setRollbackOnly()
        }
    }

    companion object{

        private val logger = LoggerFactory.getLogger(this::class.java)
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
//                withLogConsumer(Slf4jLogConsumer(logger))
                addEnv("xpack.security.enabled","false")
            }.start()
            registry.add("spring.r2dbc.url"){ "r2dbc:mysql://${mysqlContainer.host}:${mysqlContainer.firstMappedPort}/animeshka" }
            registry.add("spring.elasticsearch.uris"){ "http://${elasticSearchContainer.host}:${elasticSearchContainer.firstMappedPort}" }
        }

    }

}