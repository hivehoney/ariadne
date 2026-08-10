package com.ariadne.backend.storage.sync.provider.fake

import com.ariadne.backend.storage.domain.StorageSource
import com.ariadne.backend.storage.domain.StorageSourceType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class FakeStorageProviderTest {

    private val provider = FakeStorageProvider()

    @Test
    fun `Google Drive StorageSource의 파일 Metadata를 반환한다`() {
        val storageSource = StorageSource(
            type = StorageSourceType.GOOGLE_DRIVE,
            displayName = "Test Google Drive",
        )

        val metadata = provider.initialSync(storageSource)

        assertEquals(2, metadata.size)

        assertEquals("fake-file-001", metadata[0].externalId)
        assertEquals("resume.pdf", metadata[0].name)
        assertEquals("application/pdf", metadata[0].mimeType)
        assertEquals(1024L, metadata[0].size)

        assertEquals("fake-file-002", metadata[1].externalId)
        assertEquals("profile.jpg", metadata[1].name)
    }

    @Test
    fun `지원하지 않는 StorageSource 타입이면 실패한다`() {
        val storageSource = StorageSource(
            type = StorageSourceType.WINDOWS,
            displayName = "Test Windows",
        )

        assertThrows(IllegalArgumentException::class.java) {
            provider.initialSync(storageSource)
        }
    }
}