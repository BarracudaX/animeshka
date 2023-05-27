package com.arslan.animeshka.controller

import com.arslan.animeshka.ContentAlreadyUnderModerationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ControllerAdvise {

    @ExceptionHandler(ContentAlreadyUnderModerationException::class)
    fun onContentAlreadyUnderModerationException() : ResponseEntity<Unit> = ResponseEntity.status(HttpStatus.CONFLICT).build()

}