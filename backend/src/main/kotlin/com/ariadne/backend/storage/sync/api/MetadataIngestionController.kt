package com.ariadne.backend.storage.sync.api

import com.ariadne.backend.storage.sync.application.MetadataIngestionService
import com.ariadne.backend.storage.sync.provider.StorageFileMetadata
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Android가 Provider 독립 포맷으로 변환한 File Metadata 수신 요청을 처리한다.
 *
 * 외부 Storage를 직접 조회하는 기존 Pull 방식 Sync(/sync)와 달리,
 * 이미 변환된 Metadata를 Push 방식으로 전달받는다.
 */
@RestController
@RequestMapping("/api/storage-sources")
class MetadataIngestionController(
    private val metadataIngestionService: MetadataIngestionService,
) {
    @PostMapping("/{storageSourceId}/metadata")
    fun ingest(
        @PathVariable storageSourceId: Long,
        @Valid @RequestBody request: MetadataIngestionRequestDto,
    ): ResponseEntity<Void> {
        val metadata =
            request.files.map { file ->
                StorageFileMetadata(
                    externalId = file.externalId,
                    name = file.name,
                    mimeType = file.mimeType,
                    size = file.size,
                    // 공백 문자열은 path가 없는 것과 동일하게 취급한다.
                    path = file.path?.takeIf { it.isNotBlank() },
                    modifiedAt = file.modifiedAt,
                )
            }

        metadataIngestionService.ingest(storageSourceId, metadata)

        return ResponseEntity.ok().build()
    }
}
