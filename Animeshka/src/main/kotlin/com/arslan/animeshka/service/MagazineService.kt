package com.arslan.animeshka.service

import com.arslan.animeshka.entity.Magazine

interface MagazineService {

    suspend fun insertMagazineEntry(magazine: Magazine)

}