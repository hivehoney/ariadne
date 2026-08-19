package com.ariadne.backend.storage.connection

import com.ariadne.backend.storage.domain.StorageSourceType
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class StorageConnectorResolverTest {

    @Test
    fun `Storage Type에 맞는 Connector를 반환한다`() {
        // given
        val googleConnector = FakeStorageConnector(StorageSourceType.GOOGLE_DRIVE)

        val resolver = StorageConnectorResolver(
            listOf(googleConnector),
        )

        // when
        val connector = resolver.resolve(StorageSourceType.GOOGLE_DRIVE)

        // then
        assertThat(connector).isSameAs(googleConnector)
    }

    @Test
    fun `동일한 Storage Type의 Connector가 두 개 등록되면 실패한다`() {
        // given
        val first = FakeStorageConnector(StorageSourceType.GOOGLE_DRIVE)
        val second = FakeStorageConnector(StorageSourceType.GOOGLE_DRIVE)

        // when & then
        assertThatThrownBy {
            StorageConnectorResolver(listOf(first, second))
        }.isInstanceOf(IllegalStateException::class.java)
            .hasMessageContaining("GOOGLE_DRIVE")
    }

    @Test
    fun `등록되지 않은 Storage Type을 요청하면 실패한다`() {
        // given
        val googleConnector = FakeStorageConnector(StorageSourceType.GOOGLE_DRIVE)
        val resolver = StorageConnectorResolver(listOf(googleConnector))

        val unsupportedType = StorageSourceType.entries
            .first { it != StorageSourceType.GOOGLE_DRIVE }

        // when & then
        assertThatThrownBy {
            resolver.resolve(unsupportedType)
        }.isInstanceOf(StorageConnectorNotFoundException::class.java)
            .hasMessageContaining(unsupportedType.name)
    }

    private class FakeStorageConnector(
        override val type: StorageSourceType,
    ) : StorageConnector {
        override fun connect(request: StorageConnectionRequest): StorageConnectionResult {
            throw UnsupportedOperationException("Resolver 테스트에서는 connect를 사용하지 않는다.")
        }
    }
}