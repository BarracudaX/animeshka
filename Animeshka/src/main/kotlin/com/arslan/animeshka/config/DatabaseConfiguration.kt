package com.arslan.animeshka.config

import com.arslan.animeshka.DevCleaner
import com.arslan.animeshka.repository.elastic.AnimeDocumentRepository
import com.arslan.animeshka.repository.elastic.NovelDocumentRepository
import io.r2dbc.spi.ConnectionFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
class DatabaseConfiguration {

    @Profile("dev")
    @Bean
    fun schemaDropper(connectionFactory: ConnectionFactory,animeDocumentRepository: AnimeDocumentRepository,novelDocumentRepository: NovelDocumentRepository) : DevCleaner = DevCleaner(connectionFactory,animeDocumentRepository,novelDocumentRepository)

}