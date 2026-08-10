package com.ariadne.backend.storage.sync.provider

import com.ariadne.backend.storage.domain.StorageSource
import com.ariadne.backend.storage.domain.StorageSourceType

/**
 * 외부 Storage와의 통신을 추상화한다.
 *
 * Sync Application은 Google Drive 등 개별 외부 API를 알지 않고,
 * 이 인터페이스를 통해 파일 Metadata를 조회한다.
 */
interface StorageProvider {

    /**
     * 해당 Provider가 담당하는 Storage 유형.
     */
    val type: StorageSourceType

    /**
     * Storage의 파일 Metadata 전체를 조회
     *
     * 현재 Phase에서는 Initial Sync만 지원
     */
    fun initialSync( storageSource: StorageSource,): List<StorageFileMetadata>
}