package com.arslan.animeshka.service

import com.arslan.animeshka.entity.Studio
import com.arslan.animeshka.entity.StudioEntry
import com.arslan.animeshka.repository.StudioRepository
import kotlinx.datetime.toJavaLocalDate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.annotation.Validated

@Service
@Transactional
@Validated
class StudioServiceImpl(private val studioRepository: StudioRepository) : StudioService {

    override suspend fun createStudio(studio: StudioEntry) {
        with(studio){
            studioRepository.save(Studio(studioName,japaneseName,established.toJavaLocalDate()))
        }
    }

}