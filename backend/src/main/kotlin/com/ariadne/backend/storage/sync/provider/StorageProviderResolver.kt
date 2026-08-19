package com.ariadne.backend.storage.sync.provider

import com.ariadne.backend.storage.domain.StorageSourceType
import org.springframework.stereotype.Component

/**
 * Storage 유형에 맞는 StorageProvider를 선택한다.
 *
 * Sync 로직이 개별 Provider 구현체에 직접 의존하지 않도록
 * StorageSourceType과 StorageProvider 사이의 연결을 담당한다.
 */
@Component
class StorageProviderResolver(providers: List<StorageProvider>,) {
    private val providersByType: Map<StorageSourceType, StorageProvider>

    init {
        val groupedProviders = providers.groupBy { it.type }

        require(groupedProviders.values.none { it.size > 1 }) {
            val duplicatedTypes = groupedProviders
                .filterValues { it.size > 1 }
                .keys
                .joinToString()

            "Multiple StorageProviders registered for type: $duplicatedTypes"
        }

        providersByType = providers.associateBy { it.type }
    }

    fun resolve(type: StorageSourceType): StorageProvider {
        return requireNotNull(providersByType[type]) {
            "Unsupported storage source type: $type"
        }
    }
}