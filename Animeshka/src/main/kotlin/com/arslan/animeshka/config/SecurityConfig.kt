package com.arslan.animeshka.config

import com.arslan.animeshka.entity.UserRole
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
import org.springframework.security.oauth2.server.resource.authentication.*
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers
import javax.crypto.spec.SecretKeySpec

@Configuration
class SecurityConfig {
    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity,decoder: ReactiveJwtDecoder) : SecurityWebFilterChain {
        return http
            .oauth2ResourceServer { oauth ->
                oauth.jwt { jwt ->
                    val grantedAuthorityConverter  = ReactiveJwtGrantedAuthoritiesConverterAdapter(JwtGrantedAuthoritiesConverter().apply { setAuthorityPrefix("ROLE_") })
                    val jwtConverter = ReactiveJwtAuthenticationConverter().apply {
                        setJwtGrantedAuthoritiesConverter(grantedAuthorityConverter)
                    }
                    jwt.jwtDecoder(decoder).jwtAuthenticationConverter(jwtConverter)
                }
            }.anonymous{ }
            .authorizeExchange { authorization ->
                authorization
                    .pathMatchers("/user/register","/","/user/login").permitAll()
                    .pathMatchers(HttpMethod.POST,"/anime").authenticated()
                    .pathMatchers(HttpMethod.PUT,"/anime/*/accept").hasRole(UserRole.ANIME_ADMINISTRATOR.name)
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