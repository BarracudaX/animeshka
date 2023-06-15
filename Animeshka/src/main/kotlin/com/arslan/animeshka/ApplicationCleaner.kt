package com.arslan.animeshka

import com.arslan.animeshka.repository.elastic.AnimeDocumentRepository
import com.arslan.animeshka.repository.elastic.CharacterDocumentRepository
import com.arslan.animeshka.repository.elastic.NovelDocumentRepository
import com.arslan.animeshka.repository.elastic.PeopleDocumentRepository
import io.r2dbc.spi.ConnectionFactory
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.deleteExisting
import kotlin.io.path.isRegularFile

@Profile("dev")
@Component
class ApplicationCleaner(
        private val connectionFactory: ConnectionFactory,
        private val animeDocumentRepository: AnimeDocumentRepository,
        private val novelDocumentRepository: NovelDocumentRepository,
        private val peopleDocumentRepository: PeopleDocumentRepository,
        private val characterDocumentRepository: CharacterDocumentRepository
) : DisposableBean{

    @Value("\${image.path.location}")
    private lateinit var imageLocation: Path

    override fun destroy() {
        runBlocking{
            val connection = connectionFactory.create().awaitFirst()
            connection.createStatement("DROP TABLE IF EXISTS IMAGES").execute().awaitFirst()
            connection.createStatement("DROP TABLE IF EXISTS CONTENT_CHANGES").execute().awaitFirst()
            connection.createStatement("DROP TABLE IF EXISTS ANIME_EPISODE_CHARACTER_APPEARANCES").execute().awaitFirst()
            connection.createStatement("DROP TABLE IF EXISTS NOVEL_ANIME_RELATIONS").execute().awaitFirst()
            connection.createStatement("DROP TABLE IF EXISTS ANIME_ANIME_RELATIONS").execute().awaitFirst()
            connection.createStatement("DROP TABLE IF EXISTS NOVEL_NOVEL_RELATIONS").execute().awaitFirst()
            connection.createStatement("DROP TABLE IF EXISTS ANIME_THEMES").execute().awaitFirst()
            connection.createStatement("DROP TABLE IF EXISTS NOVEL_THEMES").execute().awaitFirst()
            connection.createStatement("DROP TABLE IF EXISTS NOVEL_GENRES").execute().awaitFirst()
            connection.createStatement("DROP TABLE IF EXISTS ANIME_GENRES").execute().awaitFirst()
            connection.createStatement("DROP TABLE IF EXISTS ANIME_CHARACTERS").execute().awaitFirst()
            connection.createStatement("DROP TABLE IF EXISTS NOVEL_CHARACTERS").execute().awaitFirst()
            connection.createStatement("DROP TABLE IF EXISTS ADAPTATIONS").execute().awaitFirst()
            connection.createStatement("DROP TABLE IF EXISTS ANIME_EPISODES").execute().awaitFirst()
            connection.createStatement("DROP TABLE IF EXISTS ANIME").execute().awaitFirst()
            connection.createStatement("DROP TABLE IF EXISTS ANIME_SEASONS").execute().awaitFirst()
            connection.createStatement("DROP TABLE IF EXISTS STUDIOS").execute().awaitFirst()
            connection.createStatement("DROP TABLE IF EXISTS NOVELS").execute().awaitFirst()
            connection.createStatement("DROP TABLE IF EXISTS NOVEL_MAGAZINES").execute().awaitFirst()
            connection.createStatement("DROP TABLE IF EXISTS CHARACTERS").execute().awaitFirst()
            connection.createStatement("DROP TABLE IF EXISTS PEOPLE").execute().awaitFirst()
            connection.createStatement("DROP TABLE IF EXISTS CONTENT").execute().awaitFirst()
            connection.createStatement("DROP TABLE IF EXISTS USERS").execute().awaitFirst()

            Files.newDirectoryStream(imageLocation){ path -> path.isRegularFile() }.use { stream ->
                for(path in stream){
                    path.deleteExisting()
                }
            }
            animeDocumentRepository.deleteAll().awaitFirstOrNull()
            novelDocumentRepository.deleteAll().awaitFirstOrNull()
            characterDocumentRepository.deleteAll().awaitFirstOrNull()
            peopleDocumentRepository.deleteAll().awaitFirstOrNull()
        }
    }


}