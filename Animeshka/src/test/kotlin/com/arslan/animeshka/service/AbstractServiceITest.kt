package com.arslan.animeshka.service

import com.arslan.animeshka.AbstractTest
import kotlinx.coroutines.test.runTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.containers.MySQLR2DBCDatabaseContainer
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.Locale

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

        private val mysqlContainer = MySQLContainer("mysql:latest")
            .withReuse(true)
            .withDatabaseName("animeshka")
            .withUsername("root")
            .withPassword("root")
        private val r2dbcContainer = MySQLR2DBCDatabaseContainer(mysqlContainer)

        @JvmStatic
        @DynamicPropertySource
        fun registerPropertySource(registry: DynamicPropertyRegistry){
            r2dbcContainer.start()
            registry.add("spring.r2dbc.url"){ "r2dbc:mysql://${mysqlContainer.host}:${mysqlContainer.firstMappedPort}/animeshka" }
        }

    }

    protected fun MessageSource.getMessage(code: String) : String = getMessage(code, emptyArray(),Locale.getDefault())

}