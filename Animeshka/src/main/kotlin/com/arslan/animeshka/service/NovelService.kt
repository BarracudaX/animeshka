package com.arslan.animeshka.service

import com.arslan.animeshka.NovelContent
import com.arslan.animeshka.PagedBasicNovelDTO
import com.arslan.animeshka.entity.Novel
import org.springframework.data.domain.Pageable

interface NovelService {

    suspend fun searchNovels(searchInput: String, pageable: Pageable) : PagedBasicNovelDTO

    suspend fun insertNovel(novelContent: NovelContent) : Novel

}