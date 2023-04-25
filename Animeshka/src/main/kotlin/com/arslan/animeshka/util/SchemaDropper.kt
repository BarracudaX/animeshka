package com.arslan.animeshka.util

import io.r2dbc.spi.ConnectionFactory
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.DisposableBean

class SchemaDropper(private val connectionFactory: ConnectionFactory) : DisposableBean{
    override fun destroy() = runBlocking{
        val connection = connectionFactory.create().awaitFirst()
        connection.createStatement("DROP TABLE USERS").execute().awaitFirst()
        Unit
    }


}