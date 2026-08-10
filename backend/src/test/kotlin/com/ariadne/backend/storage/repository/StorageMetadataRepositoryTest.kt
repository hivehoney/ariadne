package com.ariadne.backend.storage.repository

import com.ariadne.backend.storage.domain.File
import com.ariadne.backend.storage.domain.FileLocation
import com.ariadne.backend.storage.domain.StorageSource
import com.ariadne.backend.storage.domain.StorageSourceType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class StorageMetadataRepositoryTest @Autowired constructor(
    private val storageSourceRepository: StorageSourceRepository,
    private val fileRepository: FileRepository,
    private val fileLocationRepository: FileLocationRepository,
) {

    @Test
    fun `StorageSource File FileLocation을 저장하고 조회한다`() {
        val storageSource = storageSourceRepository.save(
            StorageSource(
                type = StorageSourceType.GOOGLE_DRIVE,
                displayName = "My Google Drive",
            ),
        )

        val file = fileRepository.save(
            File(
                name = "resume.pdf",
                mimeType = "application/pdf",
                size = 1024L,
            ),
        )

        fileLocationRepository.saveAndFlush(
            FileLocation(
                file = file,
                storageSource = storageSource,
                externalId = "google-file-001",
                path = "/Documents/resume.pdf",
                modifiedAt = Instant.parse("2026-08-09T10:00:00Z"),
            ),
        )

        val savedLocation =
            fileLocationRepository.findByStorageSource_IdAndExternalId(
                storageSourceId = requireNotNull(storageSource.id),
                externalId = "google-file-001",
            )

        assertNotNull(savedLocation)
        assertEquals("resume.pdf", savedLocation!!.file.name)
        assertEquals(storageSource.id, savedLocation.storageSource.id)
        assertEquals("google-file-001", savedLocation.externalId)
    }

    @Test
    fun `같은 StorageSource의 동일 externalId는 중복 저장할 수 없다`() {
        val storageSource = storageSourceRepository.save(
            StorageSource(
                type = StorageSourceType.GOOGLE_DRIVE,
                displayName = "My Google Drive",
            ),
        )

        val firstFile = fileRepository.save(
            File(
                name = "resume.pdf",
                mimeType = "application/pdf",
                size = 1024L,
            ),
        )

        val secondFile = fileRepository.save(
            File(
                name = "resume-copy.pdf",
                mimeType = "application/pdf",
                size = 1024L,
            ),
        )

        fileLocationRepository.saveAndFlush(
            FileLocation(
                file = firstFile,
                storageSource = storageSource,
                externalId = "google-file-001",
                path = "/Documents/resume.pdf",
                modifiedAt = null,
            ),
        )

        assertThrows(DataIntegrityViolationException::class.java) {
            fileLocationRepository.saveAndFlush(
                FileLocation(
                    file = secondFile,
                    storageSource = storageSource,
                    externalId = "google-file-001",
                    path = "/Backup/resume-copy.pdf",
                    modifiedAt = null,
                ),
            )
        }
    }
}