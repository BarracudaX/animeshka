package com.arslan.animeshka.util

import io.r2dbc.spi.ConnectionFactory
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.DisposableBean

class SchemaDropper(private val connectionFactory: ConnectionFactory) : DisposableBean{
    override fun destroy() = runBlocking{
        val connection = connectionFactory.create().awaitFirst()
        connection.createStatement("DROP TABLE IF EXISTS CONTENT_CHANGES").execute().awaitFirst()
        connection.createStatement("DROP TABLE IF EXISTS CONTENT").execute().awaitFirst()
        connection.createStatement("DROP TABLE IF EXISTS UNVERIFIED_NEW_CONTENT").execute().awaitFirst()
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
        connection.createStatement("DROP TABLE IF EXISTS USERS").execute().awaitFirst()
        Unit
    }


}