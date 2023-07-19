package com.arslan.animeshka.service

import com.arslan.animeshka.entity.Magazine
import com.arslan.animeshka.repository.MagazineRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class MagazineServiceImpl(
    private val magazineRepository: MagazineRepository
) : MagazineService {
    override suspend fun insertMagazineEntry(magazine: Magazine) {
        assert(magazine.id != null)
        magazineRepository.save(magazine.apply { isNewEntity = true })
    }
}