package com.ariadne.backend.storage.adapter

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Instant

class GoogleDriveMetadataMapperTest {

    private val mapper = GoogleDriveMetadataMapper()

    @Test
    fun `Google Drive 파일 Metadata를 공통 Metadata로 변환한다`() {
        // given
        val files = listOf(
            GoogleDriveFileMetadata(
                id = "folder-1",
                name = "Documents",
                mimeType = "application/vnd.google-apps.folder",
            ),
            GoogleDriveFileMetadata(
                id = "folder-2",
                name = "Resume",
                mimeType = "application/vnd.google-apps.folder",
                parents = listOf("folder-1"),
            ),
            GoogleDriveFileMetadata(
                id = "file-1",
                name = "resume.pdf",
                mimeType = "application/pdf",
                size = "1024",
                parents = listOf("folder-2"),
                modifiedTime = "2026-08-19T10:30:00Z",
            ),
        )

        // when
        val result = mapper.map(files)

        // then
        assertThat(result).hasSize(1)

        val metadata = result.single()
        assertThat(metadata.externalId).isEqualTo("file-1")
        assertThat(metadata.name).isEqualTo("resume.pdf")
        assertThat(metadata.mimeType).isEqualTo("application/pdf")
        assertThat(metadata.size).isEqualTo(1024L)
        assertThat(metadata.path).isEqualTo("/Documents/Resume/resume.pdf")
        assertThat(metadata.modifiedAt)
            .isEqualTo(Instant.parse("2026-08-19T10:30:00Z"))
    }

    @Test
    fun `폴더는 Sync Metadata 대상에서 제외한다`() {
        val files = listOf(
            GoogleDriveFileMetadata(
                id = "folder-1",
                name = "Documents",
                mimeType = "application/vnd.google-apps.folder",
            ),
        )

        val result = mapper.map(files)

        assertThat(result).isEmpty()
    }

    @Test
    fun `부모 Folder Metadata가 없으면 파일을 루트 경로로 처리한다`() {
        val files = listOf(
            GoogleDriveFileMetadata(
                id = "file-1",
                name = "resume.pdf",
                mimeType = "application/pdf",
                parents = listOf("unknown-root"),
            ),
        )

        val result = mapper.map(files)

        assertThat(result.single().path).isEqualTo("/resume.pdf")
    }
}