package com.ariadne.backend.storage.sync.provider.fake

import com.ariadne.backend.storage.domain.StorageSource
import com.ariadne.backend.storage.domain.StorageSourceType
import com.ariadne.backend.storage.sync.provider.StorageFileMetadata
import com.ariadne.backend.storage.sync.provider.StorageProvider
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.Instant

/**
 * 실제 외부 Storage API 없이 Metadata Sync 흐름을 검증하기 위한 Provider.
 *
 * Google Drive 연동 전까지 고정된 파일 Metadata를 반환한다.
 */
@Component
@Profile("test")
class FakeStorageProvider : StorageProvider {

    override val type: StorageSourceType = StorageSourceType.GOOGLE_DRIVE

    override fun initialSync(storageSource: StorageSource,)
        : List<StorageFileMetadata> {
            require(storageSource.type == type) { "Unsupported storage source type: ${storageSource.type}" }

        return listOf(
            StorageFileMetadata(
                externalId = "fake-file-001",
                name = "resume.pdf",
                mimeType = "application/pdf",
                size = 1024L,
                path = null,
                modifiedAt = Instant.parse("2026-08-01T10:00:00Z"),
            ),
            StorageFileMetadata(
                externalId = "fake-file-002",
                name = "profile.jpg",
                mimeType = "image/jpeg",
                size = 2048L,
                path = null,
                modifiedAt = Instant.parse("2026-08-02T11:00:00Z"),
            ),
        )
    }
}