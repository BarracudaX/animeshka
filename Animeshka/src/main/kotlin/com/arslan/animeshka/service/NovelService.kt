package com.arslan.animeshka.service

import com.arslan.animeshka.entity.Novel

interface NovelService {

    suspend fun findByTitle(title: String) : Novel

}