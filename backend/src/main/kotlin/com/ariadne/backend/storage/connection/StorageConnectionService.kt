package com.ariadne.backend.storage.connection

import com.ariadne.backend.storage.domain.StorageSource
import org.springframework.stereotype.Service

/**
 * Storage 연결 전체 흐름을 조정한다.
 *
 * 외부 Storage 통신은 Transaction 밖에서 수행하고,
 * 연결 성공 후 DB 저장만 PersistenceService의 Transaction에서 처리한다.
 */
@Service
class StorageConnectionService(
    private val connectorResolver: StorageConnectorResolver,
    private val persistenceService: StorageConnectionPersistenceService,
) {

    fun connect(request: StorageConnectionRequest): StorageSource {
        val connector = connectorResolver.resolve(request.type)

        // OAuth / 외부 Storage API 호출
        val result = connector.connect(request)

        // 외부 연결 성공 후 StorageSource + StorageCredential 저장
        return persistenceService.save(request.type, result)
    }
}