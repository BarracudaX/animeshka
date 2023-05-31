package com.arslan.animeshka.service

import com.arslan.animeshka.entity.Novel
import com.arslan.animeshka.repository.NovelRepository
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class NovelServiceImpl(private val novelRepository: NovelRepository,private val messageSource: MessageSource) : NovelService {

    override suspend fun findByTitle(title: String) : Novel {
        return novelRepository.findByTitleOrJapaneseTitle(title,title) ?: throw EmptyResultDataAccessException(messageSource.getMessage("novel.not.found.by.title.message",arrayOf(title),LocaleContextHolder.getLocale()),1)
    }

}