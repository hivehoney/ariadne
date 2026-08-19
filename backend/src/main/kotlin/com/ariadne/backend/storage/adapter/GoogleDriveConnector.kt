package com.ariadne.backend.storage.adapter

import com.ariadne.backend.storage.connection.StorageConnectionRequest
import com.ariadne.backend.storage.connection.StorageConnectionResult
import com.ariadne.backend.storage.connection.StorageConnector
import com.ariadne.backend.storage.domain.StorageCredentialType
import com.ariadne.backend.storage.domain.StorageSourceType
import org.springframework.stereotype.Component
import java.time.LocalDateTime

/**
 * Google Drive OAuth 결과를 Ariadne의 Storage 연결 결과로 변환한다.
 *
 * Token 교환 후 실제 Drive API를 호출해 연결 가능한 계정인지 확인한 뒤
 * StorageConnectionResult를 생성한다.
 */
@Component
class GoogleDriveConnector(
    private val googleDriveOAuthClient: GoogleDriveOAuthClient,
    private val googleDriveClient: GoogleDriveClient,
) : StorageConnector {

    override val type = StorageSourceType.GOOGLE_DRIVE

    override fun connect(request: StorageConnectionRequest): StorageConnectionResult {
        require(request is GoogleDriveConnectionRequest) {
            "GoogleDriveConnector requires GoogleDriveConnectionRequest."
        }

        val token = googleDriveOAuthClient.exchangeAuthorizationCode(request.authorizationCode)

        /*
         * Ariadne는 사용자 없이도 Metadata Sync를 수행해야 하므로
         * 지속적인 접근에 필요한 Refresh Token
         */
        val refreshToken = token.refreshToken
            ?: throw GoogleDriveConnectionException("Google refresh token was not issued.")

        // 실제 Drive API 호출
        val user = googleDriveClient.getCurrentUser(token.accessToken)

        val refreshExpiresAt = token.refreshTokenExpiresIn?.let {
            LocalDateTime.now().plusSeconds(it)
        }

        return StorageConnectionResult(
            displayName = user.emailAddress ?: user.displayName,
            credentialType = StorageCredentialType.OAUTH2,
            externalAccountId = user.permissionId,
            scope = token.scope,
            credentialData = GoogleDriveCredentialData(refreshToken),
            credentialSchemaVersion = GoogleDriveCredentialData.SCHEMA_VERSION,
            refreshExpiresAt = refreshExpiresAt,
        )
    }
}