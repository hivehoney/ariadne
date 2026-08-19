package com.ariadne.backend.storage.adapter

import tools.jackson.databind.PropertyNamingStrategies
import tools.jackson.databind.annotation.JsonNaming

/**
 * Refresh Token으로 새로 발급받은 Google Access Token 정보다.
 */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class GoogleDriveAccessToken(
    val accessToken: String,
    val expiresIn: Long,
    val scope: String? = null,
    val tokenType: String,
)