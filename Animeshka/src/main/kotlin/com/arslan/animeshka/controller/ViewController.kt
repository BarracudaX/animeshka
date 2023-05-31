package com.arslan.animeshka.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class ViewController {

    @GetMapping("/")
    fun homePage() : String = "index"

    @GetMapping("/user/login")
    fun loginPage() : String = "login"

    @GetMapping("/insert/anime")
    fun insertAnimePage() : String = "insert_anime"

    @GetMapping("/access_denied")
    fun accessDenied() : String = "access_denied"

    @GetMapping("/novel/{id}")
    suspend fun novelView(@PathVariable id: Long) : String = TODO()
}