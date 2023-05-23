package com.arslan.animeshka.service

import com.arslan.animeshka.entity.StudioEntry

interface StudioService {

    suspend fun createStudio(studio: StudioEntry)

}