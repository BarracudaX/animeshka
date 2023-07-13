package com.arslan.animeshka.controller

import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable

@Controller
class ViewController {

    @GetMapping("/")
    suspend fun homePage(model: Model) : String {
        roleAttributeInitializer(model)
        return "index"
    }

    @GetMapping("/user/login")
    suspend fun loginPage(model: Model) : String {
        roleAttributeInitializer(model)
        return "login"
    }

    @GetMapping("/insert/anime")
    suspend fun insertAnimePage(model: Model) : String {
        roleAttributeInitializer(model)
        return "insert_anime"
    }

    @GetMapping("/access_denied")
    suspend fun accessDenied(model: Model) : String {
        roleAttributeInitializer(model)
        return "access_denied"
    }

    @GetMapping("/novel/{id}")
    suspend fun novelView(@PathVariable id: Long) : String = TODO()

    suspend fun roleAttributeInitializer(model: Model){
        val auth = ReactiveSecurityContextHolder.getContext().awaitSingleOrNull()?.authentication
        val roles = auth?.authorities?.map { it.authority } ?: emptyList<String>()
        model.addAttribute("roles",roles)
    }
}