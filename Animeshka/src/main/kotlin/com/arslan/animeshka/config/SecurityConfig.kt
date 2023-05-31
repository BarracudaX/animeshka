package com.arslan.animeshka.config

import com.arslan.animeshka.UserRole
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSSigner
import com.nimbusds.jose.crypto.MACSigner
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken
import org.springframework.security.oauth2.server.resource.authentication.*
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers
import reactor.core.publisher.Mono
import javax.crypto.spec.SecretKeySpec

@Configuration
class SecurityConfig {

    @Bean
    fun cookieAwareBearerTokenAuthenticationConverter() : ServerAuthenticationConverter = ServerAuthenticationConverter{ exchange ->
        Mono.fromCallable {
            val cookie = exchange.request.cookies["Authorization"] ?: return@fromCallable null
            BearerTokenAuthenticationToken(cookie[0].value)
        }
    }

    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity,decoder: ReactiveJwtDecoder,bearerTokenConverter: ServerAuthenticationConverter) : SecurityWebFilterChain {
        return http
            .oauth2ResourceServer { oauth ->
                oauth.bearerTokenConverter(bearerTokenConverter)
                oauth.jwt { jwt ->
                    val grantedAuthorityConverter  = ReactiveJwtGrantedAuthoritiesConverterAdapter(JwtGrantedAuthoritiesConverter().apply { setAuthorityPrefix("ROLE_") })
                    val jwtConverter = ReactiveJwtAuthenticationConverter().apply { setJwtGrantedAuthoritiesConverter(grantedAuthorityConverter) }
                    jwt.jwtDecoder(decoder).jwtAuthenticationConverter(jwtConverter)
                }
            }.anonymous{ }
            .authorizeExchange { authorization ->
                authorization
                    .pathMatchers("/user/register","/","/user/login").permitAll()
                    .pathMatchers(HttpMethod.POST,"/anime","/studio").authenticated()
                    .pathMatchers(HttpMethod.GET,"/insert/anime","/novel/title/**").authenticated()
                    .pathMatchers(HttpMethod.PUT,"/anime").authenticated()
                    .pathMatchers(HttpMethod.PUT,"/content/*/accept","/content/*/reject","/anime/verify/*","/studio/verify/*").hasRole(UserRole.ANIME_ADMINISTRATOR.name)
                    .pathMatchers(HttpMethod.GET,"/","/login","/logout").permitAll()
                    .pathMatchers(HttpMethod.GET,"/resource/**").permitAll()
                    .anyExchange().denyAll()
            }.csrf { csrf ->
                val protectedWithCsrf = ServerWebExchangeMatchers.pathMatchers(HttpMethod.POST,"/login")
                csrf.requireCsrfProtectionMatcher(protectedWithCsrf)
            }
            .logout { logout ->
                logout.logoutUrl("/logout")
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