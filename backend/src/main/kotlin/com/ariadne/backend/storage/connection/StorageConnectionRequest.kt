package com.ariadne.backend.storage.connection

import com.ariadne.backend.storage.domain.StorageSourceType

/**
 * Storage 연결 시 Connector에 전달하는 Provider별 요청의 공통 타입이다.
 */
interface StorageConnectionRequest {
    val type: StorageSourceType
}