package com.ariadne.backend.storage.sync.provider

import com.ariadne.backend.storage.domain.StorageSourceType
import com.ariadne.backend.storage.sync.provider.fake.FakeStorageProvider
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class StorageProviderResolverTest {

    @Test
    fun `StorageSourceType에 맞는 Provider를 반환한다`() {
        val fakeProvider = FakeStorageProvider()

        val resolver = StorageProviderResolver(
            providers = listOf(fakeProvider),
        )

        val provider = resolver.resolve(
            StorageSourceType.GOOGLE_DRIVE,
        )

        assertSame(fakeProvider, provider)
    }

    @Test
    fun `지원하는 Provider가 없으면 실패한다`() {
        val resolver = StorageProviderResolver(
            providers = listOf(FakeStorageProvider()),
        )

        assertThrows(IllegalArgumentException::class.java) {
            resolver.resolve(StorageSourceType.WINDOWS)
        }
    }
}