package com.ariadne.backend.storage.adapter

import com.ariadne.backend.storage.domain.StorageSource
import com.ariadne.backend.storage.domain.StorageSourceType
import com.ariadne.backend.storage.sync.provider.StorageFileMetadata
import com.ariadne.backend.storage.sync.provider.StorageProvider
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

/**
 * Google Drive Metadata를 조회하여 Ariadne 공통 Metadata로 변환한다.
 */
@Component
@Profile("!test")
class GoogleDriveProvider(
    private val accessTokenService: GoogleDriveAccessTokenService,
    private val googleDriveClient: GoogleDriveClient,
    private val metadataMapper: GoogleDriveMetadataMapper,
) : StorageProvider {

    override val type = StorageSourceType.GOOGLE_DRIVE

    override fun initialSync(
        storageSource: StorageSource,
    ): List<StorageFileMetadata> {
        val storageSourceId = requireNotNull(storageSource.id) {
            "Google Drive sync requires persisted StorageSource."
        }

        val accessToken = accessTokenService.issue(storageSourceId)
        val files = googleDriveClient.listFiles(accessToken)

        return metadataMapper.map(files)
    }
}