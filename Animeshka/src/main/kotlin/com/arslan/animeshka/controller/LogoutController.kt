package com.arslan.animeshka.controller

import org.springframework.http.ResponseCookie
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.server.ServerWebExchange

@RequestMapping("/logout")
@Controller
class LogoutController {

    @GetMapping
    suspend fun logout(response: ServerHttpResponse) : String{
        val removeCookie = ResponseCookie.from("Authorization","").httpOnly(true).path("/").maxAge(0).build()
        response.addCookie(removeCookie)
        return "redirect:/"
    }

}