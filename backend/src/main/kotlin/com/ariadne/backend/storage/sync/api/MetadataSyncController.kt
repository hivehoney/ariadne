package com.ariadne.backend.storage.sync.api

import com.ariadne.backend.storage.sync.application.MetadataSyncService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Storage Metadata Sync 요청을 처리한다.
 */
@RestController
@RequestMapping("/api/storage-sources")
class MetadataSyncController(private val metadataSyncService: MetadataSyncService,) {

    @PostMapping("/{storageSourceId}/sync")
    fun sync( @PathVariable storageSourceId: Long,) : ResponseEntity<Void> {
        metadataSyncService.sync(storageSourceId)
        return ResponseEntity.ok().build()
    }
}