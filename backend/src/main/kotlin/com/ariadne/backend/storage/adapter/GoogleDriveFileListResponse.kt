package com.ariadne.backend.storage.adapter

/**
 * Google Drive files.list의 한 페이지 응답이다.
 */
data class GoogleDriveFileListResponse(
    val files: List<GoogleDriveFileMetadata> = emptyList(),
    val nextPageToken: String? = null,
    val incompleteSearch: Boolean = false,
)