package com.ariadne.backend.storage.sync.application

import com.ariadne.backend.storage.domain.StorageSource
import com.ariadne.backend.storage.domain.StorageSourceType
import com.ariadne.backend.storage.repository.FileLocationRepository
import com.ariadne.backend.storage.repository.FileRepository
import com.ariadne.backend.storage.repository.StorageSourceRepository
import com.ariadne.backend.storage.sync.provider.StorageFileMetadata
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.Instant

@SpringBootTest
class MetadataSyncIntegrationTest @Autowired constructor(
    private val metadataSyncService: MetadataSyncService,
    private val metadataSyncPersistenceService: MetadataSyncPersistenceService,
    private val storageSourceRepository: StorageSourceRepository,
    private val fileRepository: FileRepository,
    private val fileLocationRepository: FileLocationRepository,
) {

    @BeforeEach
    fun cleanUp() {
        fileLocationRepository.deleteAllInBatch()
        fileRepository.deleteAllInBatch()
        storageSourceRepository.deleteAllInBatch()
    }

    @Test
    fun `최초 Sync 시 File과 FileLocation을 저장한다`() {
        val storageSource = storageSourceRepository.save(
            StorageSource(
                type = StorageSourceType.GOOGLE_DRIVE,
                displayName = "Test Google Drive",
            ),
        )

        metadataSyncService.sync(
            storageSourceId = requireNotNull(storageSource.id),
        )

        assertEquals(2L, fileRepository.count())
        assertEquals(2L, fileLocationRepository.count())

        val firstLocation =
            fileLocationRepository.findByStorageSource_IdAndExternalId(
                storageSourceId = requireNotNull(storageSource.id),
                externalId = "fake-file-001",
            )

        val savedLocation = requireNotNull(firstLocation)

        val savedFile = fileRepository
            .findById(requireNotNull(savedLocation.file.id))
            .orElseThrow()

        assertEquals("resume.pdf", savedFile.name)

        val syncedStorageSource =
            storageSourceRepository
                .findById(requireNotNull(storageSource.id))
                .orElseThrow()

        assertNotNull(syncedStorageSource.lastSyncedAt)
    }

    @Test
    fun `동일한 StorageSource를 다시 Sync해도 중복 생성하지 않는다`() {
        val storageSource = storageSourceRepository.save(
            StorageSource(
                type = StorageSourceType.GOOGLE_DRIVE,
                displayName = "Test Google Drive",
            ),
        )

        val storageSourceId = requireNotNull(storageSource.id)

        metadataSyncService.sync(storageSourceId)
        metadataSyncService.sync(storageSourceId)

        assertEquals(2L, fileRepository.count())
        assertEquals(2L, fileLocationRepository.count())
    }

    @Test
    fun `동일 externalId의 Metadata가 변경되면 기존 File과 FileLocation을 갱신한다`() {
        val storageSource = storageSourceRepository.save(
            StorageSource(
                type = StorageSourceType.GOOGLE_DRIVE,
                displayName = "Test Google Drive",
            ),
        )

        val storageSourceId = requireNotNull(storageSource.id)

        metadataSyncService.sync(storageSourceId)

        val existingLocation =
            requireNotNull(
                fileLocationRepository.findByStorageSource_IdAndExternalId(
                    storageSourceId = storageSourceId,
                    externalId = "fake-file-001",
                ),
            )

        val existingLocationId = existingLocation.id
        val existingFileId = existingLocation.file.id

        val syncedAt = Instant.parse("2026-08-10T01:00:00Z")

        metadataSyncPersistenceService.persist(
            storageSourceId = storageSourceId,
            metadata = listOf(
                StorageFileMetadata(
                    externalId = "fake-file-001",
                    name = "resume-updated.pdf",
                    mimeType = "application/pdf",
                    size = 4096L,
                    path = "/updated/resume-updated.pdf",
                    modifiedAt = Instant.parse("2026-08-10T00:30:00Z"),
                ),
            ),
            syncedAt = syncedAt,
        )

        val updatedLocation =
            requireNotNull(
                fileLocationRepository.findByStorageSource_IdAndExternalId(
                    storageSourceId = storageSourceId,
                    externalId = "fake-file-001",
                ),
            )

        assertEquals(existingLocationId, updatedLocation.id)
        assertEquals(existingFileId, updatedLocation.file.id)

        val updatedFile = fileRepository
            .findById(requireNotNull(existingFileId))
            .orElseThrow()

        assertEquals("resume-updated.pdf", updatedFile.name)
        assertEquals(4096L, updatedFile.size)
        assertEquals(syncedAt, updatedFile.updatedAt)

        assertEquals(
            "/updated/resume-updated.pdf",
            updatedLocation.path,
        )

        assertEquals(2L, fileRepository.count())
        assertEquals(2L, fileLocationRepository.count())
    }
}