package com.arslan.animeshka.config

import com.arslan.animeshka.entity.UserCredentials
import com.arslan.animeshka.service.UserService
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSSigner
import com.nimbusds.jose.crypto.MACSigner
import io.r2dbc.spi.ConnectionFactory
import kotlinx.coroutines.reactor.mono
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers
import reactor.core.publisher.Mono
import javax.crypto.spec.SecretKeySpec

@Configuration
class SecurityConfig {
    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity,decoder: ReactiveJwtDecoder) : SecurityWebFilterChain {
        return http
            .oauth2ResourceServer { oauth ->
                oauth.jwt { jwt -> jwt.jwtDecoder(decoder) }
            }.anonymous{ }
            .authorizeExchange { authorization ->
                authorization
                    .pathMatchers("/user/register","/").permitAll()
                    .pathMatchers("/user/login").permitAll()
                    .pathMatchers(HttpMethod.GET,"/").permitAll()
                    .anyExchange().authenticated()
            }.csrf { csrf ->
                val csrfPathMatcher = ServerWebExchangeMatchers.pathMatchers("/csrf/**")
                csrf.requireCsrfProtectionMatcher(csrfPathMatcher)
            }.build()

    }

    @Bean
    fun jwtDecoder(@Value("\${secret.key}") key: ByteArray,jwsAlgorithm: JWSAlgorithm) : ReactiveJwtDecoder{
        val secretKey = SecretKeySpec(key,jwsAlgorithm.name)
        return NimbusReactiveJwtDecoder.withSecretKey(secretKey).macAlgorithm(MacAlgorithm.HS256).build()
    }

    @Bean
    fun jwtSigner(@Value("\${secret.key}") key: ByteArray,jwsAlgorithm: JWSAlgorithm) : JWSSigner = MACSigner(SecretKeySpec(key,jwsAlgorithm.name))

    @Bean
    fun jwtAlgorithm() : JWSAlgorithm = JWSAlgorithm.HS256

    @Bean
    fun passwordEncoder() : PasswordEncoder = BCryptPasswordEncoder()

}