package com.ariadne.backend.storage.sync.api

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.PositiveOrZero
import java.time.Instant

/**
 * Android가 Provider 독립 형태로 변환하여 전달하는 File Metadata 목록.
 *
 * Google Drive 등 Provider 고유 필드를 포함하지 않는다.
 */
data class MetadataIngestionRequestDto(
    @field:Valid
    val files: List<FileMetadataDto>,
)

/**
 * 개별 File의 Provider 독립 Metadata.
 */
data class FileMetadataDto(
    @field:NotBlank
    val externalId: String,
    @field:NotBlank
    val name: String,
    @field:NotBlank
    val mimeType: String,
    @field:PositiveOrZero
    val size: Long,
    val path: String? = null,
    val modifiedAt: Instant? = null,
)
