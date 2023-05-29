package com.arslan.animeshka.service

import com.arslan.animeshka.UnverifiedStudio

interface StudioService {

    suspend fun createStudio(studio: UnverifiedStudio)

    suspend fun verifyStudio(contentID: Long)
}