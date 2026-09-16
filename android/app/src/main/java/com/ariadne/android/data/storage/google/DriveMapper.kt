package com.ariadne.android.data.storage.google

import com.ariadne.android.data.storage.model.StorageConnection
import com.ariadne.android.data.storage.model.StorageFile
import com.ariadne.android.data.storage.model.StorageFileType
import com.ariadne.android.data.storage.model.StorageProviderType
import com.ariadne.android.data.storage.model.StorageSnapshot

/**
 * Google Drive 데이터를 공통 Storage 모델로 변환
 *
 * Google 전용 DTO를 상위 계층에 노출하지 않고,
 * 다른 Provider와 동일한 Storage 모델로 통일한다.
 */
object DriveMapper {

    // Google Drive 데이터를 공통 Storage 데이터로 변환
    fun toSnapshot( about: DriveAboutDto, files: List<DriveFileDto> ): StorageSnapshot {
        return StorageSnapshot(
            connection = toConnection(about),
            files = files.map(::toFile)
        )
    }

    // Google 계정 정보를 공통 연결 정보로 변환
    private fun toConnection( about: DriveAboutDto ): StorageConnection {
        val user = requireNotNull(about.user) {
            "Google Drive 사용자 정보를 가져오지 못했습니다."
        }

        val accountId = requireNotNull(user.permissionId) {
            "Google Drive 계정 식별자를 가져오지 못했습니다."
        }

        val limit = about.storageQuota?.limit?.toLongOrNull()
        val usage = about.storageQuota?.usage?.toLongOrNull()

        val available =
            if (limit != null && usage != null) {
                (limit - usage).coerceAtLeast(0L)
            } else {
                null
            }

        return StorageConnection(
            providerType = StorageProviderType.GOOGLE_DRIVE,
            accountId = accountId,
            name = "Google Drive",
            account = user.emailAddress
                ?: user.displayName
                ?: "Google 계정",
            availableBytes = available,
            totalBytes = limit
        )
    }

    // Google 파일 Metadata를 공통 파일로 변환
    private fun toFile( file: DriveFileDto ): StorageFile {
        return StorageFile(
            externalId = file.id,
            name = file.name,
            mimeType = file.mimeType,
            modifiedAt = file.modifiedTime,
            size = file.size?.toLongOrNull(),
            type = resolveType(file)
        )
    }

    // Google MIME Type을 공통 파일 유형으로 변환
    private fun resolveType( file: DriveFileDto ): StorageFileType {
        return when {
            file.mimeType == FOLDER_MIME_TYPE -> StorageFileType.FOLDER
            file.mimeType == PDF_MIME_TYPE -> StorageFileType.PDF
            file.mimeType == SHEET_MIME_TYPE -> StorageFileType.SHEET
            file.mimeType == DOCUMENT_MIME_TYPE -> StorageFileType.DOCUMENT
            file.name.endsWith(".hwp", ignoreCase = true) -> StorageFileType.HWP
            else -> StorageFileType.OTHER
        }
    }

    private const val FOLDER_MIME_TYPE = "application/vnd.google-apps.folder"
    private const val PDF_MIME_TYPE = "application/pdf"
    private const val SHEET_MIME_TYPE = "application/vnd.google-apps.spreadsheet"
    private const val DOCUMENT_MIME_TYPE = "application/vnd.google-apps.document"
}