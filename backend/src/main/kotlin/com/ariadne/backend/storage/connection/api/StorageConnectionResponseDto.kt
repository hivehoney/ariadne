package com.ariadne.backend.storage.connection.api

import com.ariadne.backend.storage.domain.StorageSourceType

/**
 * Storage 연결이 완료된 뒤 생성된 StorageSource 정보를 반환한다.
 */
data class StorageConnectionResponseDto(
    val storageSourceId: Long,
    val type: StorageSourceType,
    val displayName: String,
)