package com.ariadne.backend.storage.sync.application

import com.ariadne.backend.storage.repository.StorageSourceRepository
import com.ariadne.backend.storage.sync.provider.StorageProviderResolver
import org.springframework.stereotype.Service
import java.time.Instant

/**
 * Storage Metadata Sync 전체 흐름을 제어한다.
 *
 * 외부 Storage Metadata 조회와 DB 저장을 분리하여
 * 외부 API 호출 중 DB Transaction이 유지되지 않도록 한다.
 */
@Service
class MetadataSyncService(
    private val storageSourceRepository: StorageSourceRepository,
    private val storageProviderResolver: StorageProviderResolver,
    private val metadataSyncPersistenceService: MetadataSyncPersistenceService,
) {

    fun sync(storageSourceId: Long) {
        val storageSource = storageSourceRepository
            .findById(storageSourceId)
            .orElseThrow {
                IllegalArgumentException(
                    "StorageSource not found: $storageSourceId",
                )
            }

        val provider = storageProviderResolver.resolve(storageSource.type)

        // 외부 Storage 호출
        // 이 시점에는 DB Transaction을 유지하지 않는다.
        val metadata = provider.initialSync(storageSource)
        val syncedAt = Instant.now()

        // DB 변경은 별도의 Transaction에서 처리한다.
        metadataSyncPersistenceService.persist(
            storageSourceId = storageSourceId,
            metadata = metadata,
            syncedAt = syncedAt,
        )
    }
}