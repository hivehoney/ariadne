package com.ariadne.backend.storage.adapter

/**
 * Google Drive API에서 조회한 파일 Metadata다.
 *
 * 아직 Ariadne 공통 StorageFileMetadata로 변환하지 않고,
 * Google Drive 응답 구조를 그대로 표현한다.
 */
data class GoogleDriveFileMetadata(
    val id: String,
    val name: String,
    val mimeType: String,
    val size: String? = null,
    val parents: List<String> = emptyList(),
    val createdTime: String? = null,
    val modifiedTime: String? = null,
    val webViewLink: String? = null,
)