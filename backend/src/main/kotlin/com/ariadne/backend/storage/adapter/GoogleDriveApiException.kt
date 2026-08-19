package com.ariadne.backend.storage.adapter

/**
 * Google Drive API 호출에 실패한 경우 발생한다.
 */
class GoogleDriveApiException(
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause)