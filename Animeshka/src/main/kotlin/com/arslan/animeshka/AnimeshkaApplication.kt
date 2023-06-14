package com.arslan.animeshka

import com.arslan.animeshka.elastic.AnimeDocument
import com.arslan.animeshka.elastic.NovelDocument
import com.arslan.animeshka.entity.User
import com.arslan.animeshka.repository.UserRepository
import com.arslan.animeshka.repository.elastic.AnimeDocumentRepository
import com.arslan.animeshka.repository.elastic.NovelDocumentRepository
import com.arslan.animeshka.service.*
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.toKotlinLocalDate
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.LocalDate

@SpringBootApplication
class AnimeshkaApplication


fun main(args: Array<String>) : Unit = runBlocking{
    runApplication<AnimeshkaApplication>(*args)
}
