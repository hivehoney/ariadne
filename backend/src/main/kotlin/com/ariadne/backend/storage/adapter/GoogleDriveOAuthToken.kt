package com.ariadne.backend.storage.adapter

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Google OAuth Authorization Code 교환 결과다.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class GoogleDriveOAuthToken(
    @JsonProperty("access_token")
    val accessToken: String,

    @JsonProperty("expires_in")
    val expiresIn: Long,

    @JsonProperty("refresh_token")
    val refreshToken: String? = null,

    @JsonProperty("refresh_token_expires_in")
    val refreshTokenExpiresIn: Long? = null,

    val scope: String? = null,

    @JsonProperty("token_type")
    val tokenType: String,
)