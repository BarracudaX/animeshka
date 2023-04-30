package com.arslan.animeshka

import com.arslan.animeshka.entity.*
import com.arslan.animeshka.repository.AnimeRepository
import com.arslan.animeshka.repository.AnimeSeasonsRepository
import com.arslan.animeshka.repository.StudioRepository
import com.arslan.animeshka.repository.UserRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.math.BigDecimal
import java.time.Duration
import java.time.LocalDate

@SpringBootApplication
class AnimeshkaApplication

fun main(args: Array<String>) : Unit = runBlocking{
    val context = runApplication<AnimeshkaApplication>(*args)
}
