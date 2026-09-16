package com.ariadne.backend.storage.sync.api

import com.ariadne.backend.storage.sync.application.MetadataIngestionService
import com.ariadne.backend.storage.sync.provider.StorageFileMetadata
import org.junit.jupiter.api.Test
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.Instant

@WebMvcTest(MetadataIngestionController::class)
class MetadataIngestionControllerTest(
    @Autowired private val mockMvc: MockMvc,
) {
    @MockitoBean
    lateinit var metadataIngestionService: MetadataIngestionService

    @Test
    fun `Provider 독립 Metadata를 수신하면 200을 반환한다`() {
        mockMvc
            .perform(
                post("/api/storage-sources/{storageSourceId}/metadata", 1L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                          "files": [
                            {
                              "externalId": "external-file-001",
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

        verify(metadataIngestionService).ingest(
            1L,
            listOf(
                StorageFileMetadata(
                    externalId = "external-file-001",
                    name = "resume.pdf",
                    mimeType = "application/pdf",
                    size = 1024L,
                    path = "/Documents/resume.pdf",
                    modifiedAt = Instant.parse("2026-08-01T10:00:00Z"),
                ),
            ),
        )
    }

    @Test
    fun `externalId가 비어있으면 400을 반환한다`() {
        mockMvc
            .perform(
                post("/api/storage-sources/{storageSourceId}/metadata", 1L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                          "files": [
                            {
                              "externalId": "",
                              "name": "resume.pdf",
                              "mimeType": "application/pdf",
                              "size": 1024
                            }
                          ]
                        }
                        """.trimIndent(),
                    ),
            ).andExpect(status().isBadRequest)

        verifyNoInteractions(metadataIngestionService)
    }

    @Test
    fun `size가 음수이면 400을 반환한다`() {
        mockMvc
            .perform(
                post("/api/storage-sources/{storageSourceId}/metadata", 1L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                          "files": [
                            {
                              "externalId": "external-file-001",
                              "name": "resume.pdf",
                              "mimeType": "application/pdf",
                              "size": -1
                            }
                          ]
                        }
                        """.trimIndent(),
                    ),
            ).andExpect(status().isBadRequest)

        verifyNoInteractions(metadataIngestionService)
    }
}
