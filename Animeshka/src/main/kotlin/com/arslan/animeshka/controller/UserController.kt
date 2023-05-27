package com.arslan.animeshka.controller

import com.arslan.animeshka.UserCredentials
import com.arslan.animeshka.UserRegistration
import com.arslan.animeshka.service.UserService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RequestMapping("/user", consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
@RestController
class UserController(private val userService: UserService) {

    @PostMapping("/register")
    suspend fun registerUser(@Valid @RequestBody userRegistration: UserRegistration) : ResponseEntity<String>{
        userService.register(userRegistration)
        return ResponseEntity.ok("Success")
    }

    @PostMapping("/login")
    suspend fun login(@Valid @RequestBody userCredentials: UserCredentials) : ResponseEntity<String>{
        return ResponseEntity.ok(userService.login(userCredentials))
    }

}