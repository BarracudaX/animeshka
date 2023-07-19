package com.arslan.animeshka

/**
 * Indicates that content cannot be accepted for moderation because it's either already under moderation or has been moderated.
 */
class ContentAlreadyUnderModerationException : RuntimeException()

class EntitiesNotFoundException(val messages: List<String>) : RuntimeException()