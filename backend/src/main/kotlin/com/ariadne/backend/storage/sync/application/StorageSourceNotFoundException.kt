package com.ariadne.backend.storage.sync.application

/**
 * StorageSource 예외 상황 명확화
 */
class StorageSourceNotFoundException(
    val storageSourceId: Long,
) : RuntimeException("StorageSource not found: $storageSourceId",)