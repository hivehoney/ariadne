package com.ariadne.backend.storage.sync.application

import com.ariadne.backend.storage.sync.provider.StorageFileMetadata
import org.springframework.stereotype.Service
import java.time.Instant

/**
 * Android가 이미 Provider 독립 형태로 변환해 전달한 Metadata를
 * 기존 File/FileLocation 영속 로직에 반영한다.
 *
 * 외부 Storage 호출이 없는 흐름이므로 별도 조정 없이
 * 바로 MetadataSyncPersistenceService의 Transaction 경계로 위임한다.
 */
@Service
class MetadataIngestionService(
    private val metadataSyncPersistenceService: MetadataSyncPersistenceService,
) {
    fun ingest(
        storageSourceId: Long,
        metadata: List<StorageFileMetadata>,
    ) {
        metadataSyncPersistenceService.persist(
            storageSourceId = storageSourceId,
            metadata = metadata,
            syncedAt = Instant.now(),
        )
    }
}
