package com.arslan.animeshka.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.support.WebExchangeBindException

@ControllerAdvice
class ControllerAdviser{

    @ExceptionHandler(WebExchangeBindException::class)
    suspend fun bindExceptionHandler(ex: WebExchangeBindException) : ResponseEntity<List<String>>{
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.bindingResult.allErrors.mapNotNull { it.defaultMessage })
    }

}