package com.arslan.animeshka.service

import com.arslan.animeshka.PagedBasicStudioDTO
import com.arslan.animeshka.StudioContent
import com.arslan.animeshka.entity.Studio
import org.springframework.data.domain.Pageable

interface StudioService {

    suspend fun insertStudio(studioContent: StudioContent) : Studio
    suspend fun search(searchKey: String, pageable: Pageable): PagedBasicStudioDTO

}