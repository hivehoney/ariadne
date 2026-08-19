package com.ariadne.backend.storage.sync.application

import com.ariadne.backend.storage.domain.File
import com.ariadne.backend.storage.domain.FileLocation
import com.ariadne.backend.storage.repository.FileLocationRepository
import com.ariadne.backend.storage.repository.FileRepository
import com.ariadne.backend.storage.repository.StorageSourceRepository
import com.ariadne.backend.storage.sync.provider.StorageFileMetadata
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

/**
 * Metadata Sync 과정에서 발생하는 DB 변경을 담당한다.
 *
 * 하나의 Sync 결과를 하나의 Transaction에서 반영하여
 * File, FileLocation, StorageSource의 상태를 함께 관리한다.
 */
@Service
class MetadataSyncPersistenceService(
    private val storageSourceRepository: StorageSourceRepository,
    private val fileRepository: FileRepository,
    private val fileLocationRepository: FileLocationRepository,
) {

    @Transactional
    fun persist(storageSourceId: Long, metadata: List<StorageFileMetadata>, syncedAt: Instant,) {
        val storageSource = storageSourceRepository
            .findById(storageSourceId)
            .orElseThrow { StorageSourceNotFoundException(storageSourceId) }

        metadata.forEach { item ->
            val existingLocation = fileLocationRepository.findByStorageSource_IdAndExternalId(
                    storageSourceId = storageSourceId,
                    externalId = item.externalId,
                )

            if (existingLocation == null) {
                val file = fileRepository.save(
                    File(
                        name = item.name,
                        mimeType = item.mimeType,
                        size = item.size,
                    ),
                )

                fileLocationRepository.save(
                    FileLocation(
                        file = file,
                        storageSource = storageSource,
                        externalId = item.externalId,
                        path = item.path,
                        modifiedAt = item.modifiedAt,
                    ),
                )
            } else {
                existingLocation.file.updateMetadata(
                    name = item.name,
                    mimeType = item.mimeType,
                    size = item.size,
                    updatedAt = syncedAt,
                )

                existingLocation.updateMetadata(
                    path = item.path,
                    modifiedAt = item.modifiedAt,
                )
            }
        }

        storageSource.markSynced(syncedAt)
    }
}