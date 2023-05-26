package com.arslan.animeshka.config

import kotlinx.serialization.json.Json
import org.javers.core.Javers
import org.javers.core.JaversBuilder
import org.springframework.boot.web.codec.CodecCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.http.codec.ServerCodecConfigurer

@Configuration
class GeneralConfig {

    @Bean
    fun javers() : Javers = JaversBuilder.javers().build()

    @Bean
    fun json() : Json = Json.Default
}