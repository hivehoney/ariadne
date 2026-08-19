package com.ariadne.backend.storage.adapter

/**
 * Google Drive 연결에 필요한 인증정보를 확보하지 못한 경우 발생한다.
 */
class GoogleDriveConnectionException(
    message: String,
) : RuntimeException(message)