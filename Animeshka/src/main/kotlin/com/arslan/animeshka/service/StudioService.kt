package com.arslan.animeshka.service

import com.arslan.animeshka.StudioEntry

interface StudioService {

    suspend fun createStudio(studio: StudioEntry)

}