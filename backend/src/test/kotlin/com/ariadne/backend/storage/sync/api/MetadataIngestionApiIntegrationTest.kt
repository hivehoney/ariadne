package com.ariadne.backend.storage.sync.api

import com.ariadne.backend.storage.domain.StorageSource
import com.ariadne.backend.storage.domain.StorageSourceType
import com.ariadne.backend.storage.repository.FileLocationRepository
import com.ariadne.backend.storage.repository.FileRepository
import com.ariadne.backend.storage.repository.StorageSourceRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class MetadataIngestionApiIntegrationTest
    @Autowired
    constructor(
        private val mockMvc: MockMvc,
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
        fun `Provider 독립 Metadata를 수신하면 File과 FileLocation을 저장한다`() {
            val storageSource =
                storageSourceRepository.save(
                    StorageSource(
                        type = StorageSourceType.ANDROID,
                        displayName = "Test Device",
                    ),
                )

            mockMvc
                .perform(
                    post(
                        "/api/storage-sources/{storageSourceId}/metadata",
                        requireNotNull(storageSource.id),
                    ).contentType(MediaType.APPLICATION_JSON)
                        .content(
                            """
                            {
                              "files": [
                                {
                                  "externalId": "ingest-file-001",
                                  "name": "resume.pdf",
                                  "mimeType": "application/pdf",
                                  "size": 1024,
                                  "path": "/Documents/resume.pdf",
                                  "modifiedAt": "2026-08-01T10:00:00Z"
                                }
                              ]
                            }
                            """.trimIndent(),
                        ),
                ).andExpect(status().isOk)

            assertEquals(1L, fileRepository.count())
            assertEquals(1L, fileLocationRepository.count())

            val savedLocation =
                requireNotNull(
                    fileLocationRepository.findByStorageSourceIdAndExternalId(
                        storageSourceId = requireNotNull(storageSource.id),
                        externalId = "ingest-file-001",
                    ),
                )

            // FileLocation.file은 Lazy 연관관계이므로, 조회 Transaction이 끝난 뒤
            // 값을 확인하려면 FileRepository로 다시 조회해야 한다.
            val savedFile =
                fileRepository
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
        fun `동일 externalId를 다시 수신하면 중복 생성 없이 기존 File과 FileLocation을 갱신한다`() {
            val storageSource =
                storageSourceRepository.save(
                    StorageSource(
                        type = StorageSourceType.ANDROID,
                        displayName = "Test Device",
                    ),
                )
            val storageSourceId = requireNotNull(storageSource.id)

            mockMvc
                .perform(
                    post("/api/storage-sources/{storageSourceId}/metadata", storageSourceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                            """
                            {
                              "files": [
                                {
                                  "externalId": "ingest-file-001",
                                  "name": "resume.pdf",
                                  "mimeType": "application/pdf",
                                  "size": 1024,
                                  "path": "/Documents/resume.pdf",
                                  "modifiedAt": "2026-08-01T10:00:00Z"
                                }
                              ]
                            }
                            """.trimIndent(),
                        ),
                ).andExpect(status().isOk)

            mockMvc
                .perform(
                    post("/api/storage-sources/{storageSourceId}/metadata", storageSourceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                            """
                            {
                              "files": [
                                {
                                  "externalId": "ingest-file-001",
                                  "name": "resume-updated.pdf",
                                  "mimeType": "application/pdf",
                                  "size": 4096,
                                  "path": "/Documents/resume-updated.pdf",
                                  "modifiedAt": "2026-08-02T10:00:00Z"
                                }
                              ]
                            }
                            """.trimIndent(),
                        ),
                ).andExpect(status().isOk)

            assertEquals(1L, fileRepository.count())
            assertEquals(1L, fileLocationRepository.count())

            val updatedLocation =
                requireNotNull(
                    fileLocationRepository.findByStorageSourceIdAndExternalId(
                        storageSourceId = storageSourceId,
                        externalId = "ingest-file-001",
                    ),
                )

            val updatedFile =
                fileRepository
                    .findById(requireNotNull(updatedLocation.file.id))
                    .orElseThrow()

            assertEquals("resume-updated.pdf", updatedFile.name)
            assertEquals(4096L, updatedFile.size)
            assertEquals("/Documents/resume-updated.pdf", updatedLocation.path)
        }

        @Test
        fun `존재하지 않는 StorageSource로 요청하면 404를 반환한다`() {
            mockMvc
                .perform(
                    post("/api/storage-sources/{storageSourceId}/metadata", 999999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                            """
                            {
                              "files": [
                                {
                                  "externalId": "ingest-file-001",
                                  "name": "resume.pdf",
                                  "mimeType": "application/pdf",
                                  "size": 1024
                                }
                              ]
                            }
                            """.trimIndent(),
                        ),
                ).andExpect(status().isNotFound)

            assertEquals(0L, fileRepository.count())
            assertEquals(0L, fileLocationRepository.count())
        }
    }
