package com.arslan.animeshka.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class ViewController {

    @GetMapping("/")
    fun homePage() : String = "index"

    @GetMapping("/login")
    fun loginPage() : String = "login"
}