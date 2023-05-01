package com.arslan.animeshka.controller

import com.arslan.animeshka.AbstractTest
import com.arslan.animeshka.config.SecurityConfig
import com.arslan.animeshka.service.UserService
import com.ninjasquad.springmockk.MockkBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Import
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.client.WebClient

@WebFluxTest
@Import(SecurityConfig::class)
abstract class AbstractControllerUnitTest : AbstractTest() {

    @MockkBean
    protected lateinit var userService: UserService

    @Autowired
    protected lateinit var webClient: WebTestClient

    @Autowired
    protected lateinit var messageSource: MessageSource
}