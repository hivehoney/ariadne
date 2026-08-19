package com.ariadne.backend.storage.adapter

import com.ariadne.backend.storage.connection.StorageConnectionRequest
import com.ariadne.backend.storage.domain.StorageSourceType

/**
 * Android에서 전달받은 Google Server Authorization Code를 담는다.
 */
data class GoogleDriveConnectionRequest(
    val authorizationCode: String,
) : StorageConnectionRequest {

    override val type = StorageSourceType.GOOGLE_DRIVE

    init {
        require(authorizationCode.isNotBlank()) {
            "Google authorization code must not be blank."
        }
    }
}