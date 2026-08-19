package com.ariadne.backend.storage.adapter

import com.ariadne.backend.storage.credential.CredentialDataCodec
import com.ariadne.backend.storage.credential.CredentialRefreshPersistenceService
import com.ariadne.backend.storage.credential.StorageCredentialNotFoundException
import com.ariadne.backend.storage.domain.StorageCredentialStatus
import com.ariadne.backend.storage.domain.StorageCredentialType
import com.ariadne.backend.storage.repository.StorageCredentialRepository
import org.springframework.stereotype.Service

/**
 * 저장된 Google Refresh Token을 사용해 새 Access Token을 발급한다.
 *
 * Access Token은 DB에 저장하지 않고 호출 결과로만 반환한다.
 */
@Service
class GoogleDriveAccessTokenService(
    private val storageCredentialRepository: StorageCredentialRepository,
    private val credentialDataCodec: CredentialDataCodec,
    private val googleDriveOAuthClient: GoogleDriveOAuthClient,
    private val refreshPersistenceService: CredentialRefreshPersistenceService,
) {

    fun issue(storageSourceId: Long): String {
        val credential = storageCredentialRepository.findByStorageSource_Id(storageSourceId)
            ?: throw StorageCredentialNotFoundException(storageSourceId)

        check(credential.credentialType == StorageCredentialType.OAUTH2) {
            "Google Drive credential must be OAuth2."
        }

        check(credential.credentialStatus == StorageCredentialStatus.ACTIVE) {
            "Google Drive credential is not active."
        }

        val credentialData = credentialDataCodec.decode(
            credential.credentialData,
            GoogleDriveCredentialData::class,
        )

        val token = googleDriveOAuthClient.refreshAccessToken(
            credentialData.refreshToken,
        )

        refreshPersistenceService.markRefreshed(storageSourceId)

        return token.accessToken
    }
}