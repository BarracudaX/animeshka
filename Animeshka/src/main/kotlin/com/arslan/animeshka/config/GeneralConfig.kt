package com.arslan.animeshka.config

import kotlinx.serialization.json.Json
import org.springframework.boot.web.codec.CodecCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.http.codec.json.KotlinSerializationJsonDecoder

@Configuration
class GeneralConfig {

    @Bean
    fun json() : Json = Json{ encodeDefaults = true }

}