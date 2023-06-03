package com.arslan.animeshka.service

import com.arslan.animeshka.StudioContent
import com.arslan.animeshka.entity.Content

interface StudioService {

    suspend fun createStudio(studio: StudioContent) : Content

    suspend fun verifyStudio(contentID: Long)
}