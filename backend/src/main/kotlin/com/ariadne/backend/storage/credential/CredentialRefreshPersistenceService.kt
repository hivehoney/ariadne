package com.ariadne.backend.storage.credential

import com.ariadne.backend.storage.repository.StorageCredentialRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Credential Refresh와 관련된 DB 변경을 처리한다.
 */
@Service
class CredentialRefreshPersistenceService(
    private val storageCredentialRepository: StorageCredentialRepository,
) {

    @Transactional
    fun markRefreshed(storageSourceId: Long) {
        val credential = storageCredentialRepository.findByStorageSource_Id(storageSourceId)
            ?: throw StorageCredentialNotFoundException(storageSourceId)

        credential.markRefreshed()
    }
}