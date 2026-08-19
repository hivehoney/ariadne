package com.ariadne.backend.storage.connection.api

import com.ariadne.backend.storage.adapter.GoogleDriveConnectionRequest
import com.ariadne.backend.storage.connection.StorageConnectionService
import com.ariadne.backend.storage.domain.StorageSource
import com.ariadne.backend.storage.domain.StorageSourceType
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(StorageConnectionController::class)
class StorageConnectionControllerTest(
    @Autowired private val mockMvc: MockMvc,
) {

    @MockitoBean
    lateinit var storageConnectionService: StorageConnectionService

    @Test
    fun `Google Drive를 연결한다`() {
        // given
        val request = GoogleDriveConnectionRequest("google-authorization-code")
        val storageSource = mock(StorageSource::class.java)

        `when`(storageSource.id).thenReturn(1L)
        `when`(storageSource.type).thenReturn(StorageSourceType.GOOGLE_DRIVE)
        `when`(storageSource.displayName).thenReturn("taeuk@example.com")
        `when`(storageConnectionService.connect(request)).thenReturn(storageSource)

        // when & then
        mockMvc.perform(
            post("/api/storage-connections/google-drive")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "authorizationCode": "google-authorization-code"
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.storageSourceId").value(1))
            .andExpect(jsonPath("$.type").value("GOOGLE_DRIVE"))
            .andExpect(jsonPath("$.displayName").value("taeuk@example.com"))
    }

    @Test
    fun `Authorization Code가 비어있으면 연결 요청에 실패한다`() {
        mockMvc.perform(
            post("/api/storage-connections/google-drive")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "authorizationCode": ""
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isBadRequest)

        verifyNoInteractions(storageConnectionService)
    }
}