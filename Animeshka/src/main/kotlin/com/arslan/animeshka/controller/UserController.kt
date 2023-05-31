package com.arslan.animeshka.controller

import com.arslan.animeshka.UserCredentials
import com.arslan.animeshka.UserRegistration
import com.arslan.animeshka.service.UserService
import kotlinx.coroutines.reactive.awaitFirst
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.method.support.ModelAndViewContainer
import org.springframework.web.reactive.result.view.View
import org.springframework.web.server.ServerWebExchange
import java.time.Duration
import javax.validation.Valid

@RequestMapping("/user", consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
@Controller
class UserController(private val userService: UserService,@Value("\${jwt.token.duration}") private val tokenDuration: Duration) {

    @PostMapping("/register")
    suspend fun registerUser(@Valid @RequestBody userRegistration: UserRegistration) : ResponseEntity<String>{
        userService.register(userRegistration)
        return ResponseEntity.ok("Success")
    }

    @PostMapping("/login", consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE])
    suspend fun login(exchange: ServerWebExchange,response: ServerHttpResponse): String{
        val (email,password) = exchange.formData.awaitFirst().let { params -> params["email"]!![0] to params["password"]!![0] }
        val token = userService.login(UserCredentials(email,password))
        response.addCookie(ResponseCookie.from("Authorization",token).httpOnly(true).path("/").maxAge(tokenDuration).build())
        return "redirect:/"
    }

}