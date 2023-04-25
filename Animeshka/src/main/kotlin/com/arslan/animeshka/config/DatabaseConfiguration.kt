package com.arslan.animeshka.config

import com.arslan.animeshka.util.SchemaDropper
import io.r2dbc.spi.ConnectionFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
class DatabaseConfiguration {

    @Profile("dev")
    @Bean
    fun schemaDropper(connectionFactory: ConnectionFactory) : SchemaDropper = SchemaDropper(connectionFactory)

}