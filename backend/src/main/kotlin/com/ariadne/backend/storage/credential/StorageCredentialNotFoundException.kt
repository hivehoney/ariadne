package com.ariadne.backend.storage.credential

/**
 * StorageSource에 연결된 인증정보가 존재하지 않을 때 발생한다.
 */
class StorageCredentialNotFoundException(
    storageSourceId: Long,
) : RuntimeException(
    "Storage credential not found for storage source: $storageSourceId",
)