package com.ariadne.android.data.storage.google

/**
 * Google Drive about API 응답
 */
data class DriveAboutDto(
    val user: DriveUserDto? = null,
    val storageQuota: DriveQuotaDto? = null
)

/**
 * Google Drive 사용자 정보
 */
data class DriveUserDto(
    val displayName: String? = null,
    val emailAddress: String? = null,
    val permissionId: String? = null
)

/**
 * Google Drive 계정 용량 정보
 */
data class DriveQuotaDto(
    val limit: String? = null,
    val usage: String? = null
)

/**
 * Google Drive 파일 목록 API 응답
 */
data class DriveFileListResponse(
    val files: List<DriveFileDto> = emptyList(),
    val nextPageToken: String? = null
)

/**
 * Google Drive 파일 Metadata
 */
data class DriveFileDto(
    val id: String,
    val name: String,
    val mimeType: String,
    val size: String? = null,
    val modifiedTime: String? = null
)

