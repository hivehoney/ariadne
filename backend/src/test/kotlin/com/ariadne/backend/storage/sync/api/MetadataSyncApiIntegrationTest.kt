package com.ariadne.backend.storage.sync.api

import com.ariadne.backend.storage.domain.StorageSource
import com.ariadne.backend.storage.domain.StorageSourceType
import com.ariadne.backend.storage.repository.FileLocationRepository
import com.ariadne.backend.storage.repository.FileRepository
import com.ariadne.backend.storage.repository.StorageSourceRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class MetadataSyncApiIntegrationTest @Autowired constructor(
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
    fun `StorageSource Metadata Sync API를 호출한다`() {
        val storageSource = storageSourceRepository.save(
            StorageSource(
                type = StorageSourceType.GOOGLE_DRIVE,
                displayName = "Test Google Drive",
            ),
        )

        mockMvc.perform(
            post(
                "/api/storage-sources/{storageSourceId}/sync",
                requireNotNull(storageSource.id),
            ),
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `존재하지 않는 StorageSource를 Sync하면 404를 반환한다`() {
        mockMvc.perform(
            post(
                "/api/storage-sources/{storageSourceId}/sync",
                999999L,
            ),
        )
            .andExpect(status().isNotFound)
    }
}