package com.ariadne.backend.storage.repository

import com.ariadne.backend.storage.domain.FileLocation
import org.springframework.data.jpa.repository.JpaRepository

interface FileLocationRepository : JpaRepository<FileLocation, Long> {
    fun findByStorageSourceIdAndExternalId(
        storageSourceId: Long,
        externalId: String,
    ): FileLocation?
}
