package com.arslan.animeshka.service

import com.arslan.animeshka.StudioContent
import com.arslan.animeshka.entity.Studio

interface StudioService {

    suspend fun insertStudio(studioContent: StudioContent) : Studio
}