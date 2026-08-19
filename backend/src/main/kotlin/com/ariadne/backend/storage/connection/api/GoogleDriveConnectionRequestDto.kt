package com.ariadne.backend.storage.connection.api

import jakarta.validation.constraints.NotBlank

/**
 * Android에서 획득한 Google Server Authorization Code를 전달받는다.
 */
data class GoogleDriveConnectionRequestDto(
    @field:NotBlank
    val authorizationCode: String,
)