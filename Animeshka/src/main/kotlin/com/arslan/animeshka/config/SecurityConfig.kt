package com.arslan.animeshka.config

import com.arslan.animeshka.UserRole
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSSigner
import com.nimbusds.jose.crypto.MACSigner
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpCookie
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken
import org.springframework.security.oauth2.server.resource.authentication.*
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationEntryPoint
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers
import org.springframework.web.server.WebFilter
import reactor.core.publisher.Mono
import java.net.URI
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
            .exceptionHandling { exceptionHandling ->
                exceptionHandling.authenticationEntryPoint(RedirectServerAuthenticationEntryPoint("/user/login"))
                exceptionHandling.accessDeniedHandler { exchange, _ ->
                    mono {
                        exchange.response.statusCode = HttpStatus.PERMANENT_REDIRECT
                        exchange.response.headers.location = URI.create("/access_denied")
                        null
                    }
                }
            }
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
                    .pathMatchers(HttpMethod.GET,"/","/login","/logout","/access_denied").permitAll()
                    .pathMatchers(HttpMethod.GET,"/resource/**").permitAll()
                    .anyExchange().denyAll()
            }.csrf { csrf ->
                val protectedWithCsrf = ServerWebExchangeMatchers.pathMatchers(HttpMethod.POST,"/login")
                csrf.requireCsrfProtectionMatcher(protectedWithCsrf)
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

    @Bean
    fun webFilter() : WebFilter = WebFilter { exchange, chain ->

        mono {
            if(exchange.request.path.value() == "/user/login" && ReactiveSecurityContextHolder.getContext().awaitSingle().authentication !is AnonymousAuthenticationToken){
                exchange.response.statusCode = HttpStatus.PERMANENT_REDIRECT
                exchange.response.headers.location = URI.create("/")
                null
            }else{
                chain.filter(exchange).awaitSingleOrNull()
            }
        }
    }
}