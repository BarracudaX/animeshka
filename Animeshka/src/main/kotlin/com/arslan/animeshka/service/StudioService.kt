package com.arslan.animeshka.service

import com.arslan.animeshka.UnverifiedStudio
import com.arslan.animeshka.entity.UnverifiedContent

interface StudioService {

    suspend fun createStudio(studio: UnverifiedStudio) : UnverifiedContent

    suspend fun verifyStudio(contentID: Long)
}