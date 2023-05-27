package com.arslan.animeshka.service

interface ModerationService {

    suspend fun acceptModeration(contentID: Long)

    suspend fun rejectContent(contentID: Long, rejectionReason: String)
}