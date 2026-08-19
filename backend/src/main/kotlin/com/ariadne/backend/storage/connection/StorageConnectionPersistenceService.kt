package com.ariadne.backend.storage.connection

import com.ariadne.backend.storage.credential.CredentialDataCodec
import com.ariadne.backend.storage.domain.StorageCredential
import com.ariadne.backend.storage.domain.StorageSource
import com.ariadne.backend.storage.domain.StorageSourceType
import com.ariadne.backend.storage.repository.StorageCredentialRepository
import com.ariadne.backend.storage.repository.StorageSourceRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Storage 연결 결과를 StorageSource와 StorageCredential에 저장한다.
 *
 * 두 데이터는 하나의 Storage 연결을 구성하므로
 * 하나의 Transaction에서 함께 저장한다.
 */
@Service
class StorageConnectionPersistenceService(
    private val storageSourceRepository: StorageSourceRepository,
    private val storageCredentialRepository: StorageCredentialRepository,
    private val credentialDataCodec: CredentialDataCodec,
) {

    @Transactional
    fun save(
        type: StorageSourceType,
        result: StorageConnectionResult,
    ): StorageSource {
        val storageSource = storageSourceRepository.save(
            StorageSource(
                type = type,
                displayName = result.displayName,
            ),
        )

        /*
         * Provider Credential에는 Refresh Token 등 민감정보가 포함될 수 있으므로
         * Entity에 저장하기 전에 반드시 직렬화와 암호화를 수행한다.
         */
        val encryptedCredentialData = credentialDataCodec.encode(result.credentialData)

        storageCredentialRepository.save(
            StorageCredential(
                storageSource = storageSource,
                credentialType = result.credentialType,
                externalAccountId = result.externalAccountId,
                scope = result.scope,
                credentialData = encryptedCredentialData,
                credentialSchemaVersion = result.credentialSchemaVersion,
                expiresAt = result.expiresAt,
                refreshExpiresAt = result.refreshExpiresAt,
            ),
        )

        return storageSource
    }
}