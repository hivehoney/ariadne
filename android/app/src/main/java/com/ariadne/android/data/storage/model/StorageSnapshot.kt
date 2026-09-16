package com.ariadne.android.data.storage.model

/**
 * 외부 Storage에서 조회한 화면 단위 데이터 표현
 *
 * Provider별 API 응답을 공통 연결 정보와 파일 Metadata로 변환하여
 * Google Drive, OneDrive 등에서 동일한 상위 로직을 사용할 수 있게 한다.
 */
data class StorageSnapshot(
    val connection: StorageConnection,
    val files: List<StorageFile>
)

/**
 * 연결된 외부 Storage의 계정 및 용량 정보
 */
data class StorageConnection(
    val providerType: StorageProviderType,
    val accountId: String,
    val name: String,
    val account: String,
    val availableBytes: Long? = null,
    val totalBytes: Long? = null
)

/**
 * 외부 Storage의 파일 또는 폴더 Metadata
 */
data class StorageFile(
    val externalId: String,
    val name: String,
    val mimeType: String,
    val modifiedAt: String? = null,
    val size: Long? = null,
    val type: StorageFileType,
    val itemCount: Int? = null
)

/**
 * 외부 Storage Provider 식별 타입
 *
 * 현재 실제 지원 Provider만 정의하고,
 * 새로운 Provider 구현 시 값을 추가한다.
 */
enum class StorageProviderType {
    GOOGLE_DRIVE
}

/**
 * Provider에 관계없이 사용하는 공통 파일 유형 정의
 */
enum class StorageFileType {
    FOLDER,
    PDF,
    HWP,
    SHEET,
    DOCUMENT,
    OTHER
}