package com.ariadne.backend.storage.sync.api

import com.ariadne.backend.storage.sync.application.StorageSourceNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * Metadata Sync API에서 발생하는 예외를 HTTP 응답으로 변환한다.
 */
@RestControllerAdvice
class MetadataSyncExceptionHandler {

    @ExceptionHandler(StorageSourceNotFoundException::class)
    fun handleStorageSourceNotFound( exception: StorageSourceNotFoundException,): ProblemDetail {
        return ProblemDetail
            .forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                exception.message ?: "StorageSource not found",
            )
            .apply {
                title = "StorageSource Not Found"
            }
    }
}