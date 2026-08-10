package com.ariadne.backend.storage.sync.provider

import java.time.Instant

/**
 * 외부 Storage Provider에서 조회한 파일 Metadata의 공통 모델.
 *
 * Google Drive 등의 Provider 고유 응답 객체를
 * OneSearch 내부 Sync 로직에 직접 노출하지 않기 위해 사용한다.
 */
data class StorageFileMetadata(
    val externalId: String,
    val name: String,
    val mimeType: String,
    val size: Long,
    val path: String?,
    val modifiedAt: Instant?,
) {

    init {
        require(externalId.isNotBlank()) { "externalId cannot be blank" }
        require(name.isNotBlank()) { "name cannot be blank" }
        require(mimeType.isNotBlank()) { "mimeType cannot be blank" }
        require(size >= 0) { "size cannot be negative" }
        require(path == null || path.isNotBlank()) { "path cannot be blank" }
    }
}