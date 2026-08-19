package com.ariadne.backend.storage.repository

import com.ariadne.backend.storage.domain.StorageCredential
import org.springframework.data.jpa.repository.JpaRepository


interface StorageCredentialRepository : JpaRepository<StorageCredential, Long> {

    fun findByStorageSource_Id(storageSourceId: Long): StorageCredential?
}