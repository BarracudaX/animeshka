package com.arslan.animeshka

class ContentAlreadyUnderModerationException : RuntimeException()

class EntitiesNotFoundException(val messages: List<String>) : RuntimeException()