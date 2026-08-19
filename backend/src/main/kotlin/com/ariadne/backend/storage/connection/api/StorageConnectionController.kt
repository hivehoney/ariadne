package com.ariadne.backend.storage.connection.api

import com.ariadne.backend.storage.adapter.GoogleDriveConnectionRequest
import com.ariadne.backend.storage.connection.StorageConnectionService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 외부 Storage 연결 요청을 처리한다.
 */
@RestController
@RequestMapping("/api/storage-connections")
class StorageConnectionController(
    private val storageConnectionService: StorageConnectionService,
) {

    @PostMapping("/google-drive")
    fun connectGoogleDrive(
        @Valid @RequestBody request: GoogleDriveConnectionRequestDto,
    ): ResponseEntity<StorageConnectionResponseDto> {
        val storageSource = storageConnectionService.connect(
            GoogleDriveConnectionRequest(request.authorizationCode),
        )

        val storageSourceId = requireNotNull(storageSource.id) {
            "Connected StorageSource must be persisted."
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(
            StorageConnectionResponseDto(
                storageSourceId = storageSourceId,
                type = storageSource.type,
                displayName = storageSource.displayName,
            ),
        )
    }
}