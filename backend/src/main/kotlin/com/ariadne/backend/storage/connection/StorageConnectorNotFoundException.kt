package com.ariadne.backend.storage.connection

import com.ariadne.backend.storage.domain.StorageSourceType

/**
 * 요청한 Storage Type을 처리할 Connector가 등록되지 않은 경우 발생한다.
 */
class StorageConnectorNotFoundException(
    type: StorageSourceType,
) : RuntimeException(
    "Storage connector not found for type: $type",
)