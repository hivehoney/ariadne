package com.ariadne.backend.storage.adapter

/**
 * Google OAuth 통신 또는 Token 교환에 실패한 경우 발생한다.
 */
class GoogleDriveOAuthException(
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause)