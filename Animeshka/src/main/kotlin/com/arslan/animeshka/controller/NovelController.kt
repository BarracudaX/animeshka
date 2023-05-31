package com.arslan.animeshka.controller

import com.arslan.animeshka.NovelDTO
import com.arslan.animeshka.entity.Novel
import com.arslan.animeshka.service.NovelService
import com.arslan.animeshka.toDTO
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/novel")
class NovelController(private val novelService: NovelService) {

    @GetMapping("/title")
    suspend fun findByTitle(@RequestParam title: String) : ResponseEntity<NovelDTO>{
        return ResponseEntity.ok(novelService.findByTitle(title).toDTO())
    }

}