package com.ariadne.backend.storage.adapter

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestClientResponseException

/**
 * Google OAuth 서버와 통신하는 Client다.
 *
 * Android에서 전달받은 Authorization Code를
 * Access Token / Refresh Token으로 교환한다.
 */
@Component
class GoogleDriveOAuthClient(
    restClientBuilder: RestClient.Builder,

    @Value("\${ariadne.storage.google-drive.oauth.client-id}")
    private val clientId: String,

    @Value("\${ariadne.storage.google-drive.oauth.client-secret}")
    private val clientSecret: String,

    @Value("\${ariadne.storage.google-drive.oauth.redirect-uri:}")
    private val redirectUri: String,
) {

    private val restClient = restClientBuilder
        .baseUrl(GOOGLE_OAUTH_BASE_URL)
        .build()

    /**
     * Google Authorization Code를 Token으로 교환한다.
     */
    fun exchangeAuthorizationCode(authorizationCode: String): GoogleDriveOAuthToken {
        require(authorizationCode.isNotBlank()) {
            "Google authorization code must not be blank."
        }

        val formData = LinkedMultiValueMap<String, String>().apply {
            add("client_id", clientId)
            add("client_secret", clientSecret)
            add("code", authorizationCode)
            add("grant_type", "authorization_code")
            add("redirect_uri", redirectUri)
        }

        return try {
            restClient.post()
                .uri("/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(formData)
                .retrieve()
                .body(GoogleDriveOAuthToken::class.java)
                ?: throw GoogleDriveOAuthException("Google OAuth token response is empty.")
        } catch (exception: RestClientResponseException) {
            throw GoogleDriveOAuthException(
                "Google OAuth token exchange failed: HTTP ${exception.statusCode.value()}",
                exception,
            )
        } catch (exception: RestClientException) {
            throw GoogleDriveOAuthException(
                "Failed to communicate with Google OAuth server.",
                exception,
            )
        }
    }

    companion object {
        private const val GOOGLE_OAUTH_BASE_URL = "https://oauth2.googleapis.com"
    }

    fun refreshAccessToken(refreshToken: String): GoogleDriveAccessToken {
        require(refreshToken.isNotBlank()) {
            "Google refresh token must not be blank."
        }

        val formData = LinkedMultiValueMap<String, String>().apply {
            add("client_id", clientId)
            add("client_secret", clientSecret)
            add("refresh_token", refreshToken)
            add("grant_type", "refresh_token")
        }

        return try {
            restClient.post()
                .uri("/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(formData)
                .retrieve()
                .body(GoogleDriveAccessToken::class.java)
                ?: throw GoogleDriveOAuthException("Google OAuth token response is empty.")
        } catch (exception: RestClientResponseException) {
            throw GoogleDriveOAuthException(
                "Google OAuth token refresh failed: HTTP ${exception.statusCode.value()}",
                exception,
            )
        } catch (exception: RestClientException) {
            throw GoogleDriveOAuthException(
                "Failed to communicate with Google OAuth server.",
                exception,
            )
        }
    }
}