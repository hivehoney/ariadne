package com.ariadne.backend.storage.adapter

import com.ariadne.backend.storage.sync.provider.StorageFileMetadata
import org.springframework.stereotype.Component
import java.time.Instant

/**
 * Google Drive Metadata를 Ariadne 공통 Metadata로 변환한다.
 */
@Component
class GoogleDriveMetadataMapper {

    fun map(files: List<GoogleDriveFileMetadata>): List<StorageFileMetadata> {
        val fileById = files.associateBy { it.id }

        return files
            .filterNot { it.mimeType == GOOGLE_DRIVE_FOLDER_MIME_TYPE }
            .map { file ->
                StorageFileMetadata(
                    externalId = file.id,
                    name = file.name,
                    mimeType = file.mimeType,
                    size = file.size?.toLongOrNull() ?: 0L,
                    path = buildPath(file, fileById),
                    modifiedAt = file.modifiedTime?.let(Instant::parse),
                )
            }
    }

    /**
     * Google Drive는 전체 경로 대신 부모 Folder ID를 제공하므로
     * 조회된 Folder Metadata를 따라가며 Ariadne에서 사용할 경로를 만든다.
     */
    private fun buildPath(
        file: GoogleDriveFileMetadata,
        fileById: Map<String, GoogleDriveFileMetadata>,
    ): String {
        val path = mutableListOf(file.name)
        val visited = mutableSetOf(file.id)

        var current = file

        while (true) {
            val parentId = current.parents.firstOrNull() ?: break
            val parent = fileById[parentId] ?: break

            check(visited.add(parent.id)) {
                "Circular Google Drive parent relationship detected: ${file.id}"
            }

            path.add(parent.name)
            current = parent
        }

        return "/" + path.asReversed().joinToString("/")
    }

    companion object {
        private const val GOOGLE_DRIVE_FOLDER_MIME_TYPE =
            "application/vnd.google-apps.folder"
    }
}