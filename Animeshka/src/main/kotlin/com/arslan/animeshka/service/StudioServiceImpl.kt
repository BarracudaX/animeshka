package com.arslan.animeshka.service

import com.arslan.animeshka.BasicStudioDTO
import com.arslan.animeshka.PagedBasicStudioDTO
import com.arslan.animeshka.StudioContent
import com.arslan.animeshka.entity.Studio
import com.arslan.animeshka.repository.StudioRepository
import com.arslan.animeshka.repository.elastic.StudioDocumentRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.datetime.toJavaLocalDate
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.annotation.Validated

@Service
@Transactional
@Validated
class StudioServiceImpl(
        private val studioRepository: StudioRepository,
        private val studioDocumentRepository: StudioDocumentRepository
) : StudioService {


    override suspend fun insertStudio(studioContent: StudioContent): Studio {
        return with(studioContent) { studioRepository.save(Studio(studioName, japaneseName, established.toJavaLocalDate(), id!!).apply { isNewEntity = true }) }
    }

    override suspend fun search(searchKey: String, pageable: Pageable): PagedBasicStudioDTO {
        val page = studioDocumentRepository.findStudios(searchKey,pageable)
        val ids = page.content.map { it.content.id }
        return PagedBasicStudioDTO(studioRepository.findAllById(ids).map { studio -> BasicStudioDTO(studio.studioName,studio.japaneseName,studio.id) }.toList(),page.hasNext(),page.hasPrevious())
    }


}