package com.arslan.animeshka.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class ViewController {

    @GetMapping("/")
    fun homePage() : String = "index"

    @GetMapping("/user/login")
    fun loginPage() : String = "login"

    @GetMapping("/insert/anime")
    fun insertAnimePage() : String = "insert_anime"
}