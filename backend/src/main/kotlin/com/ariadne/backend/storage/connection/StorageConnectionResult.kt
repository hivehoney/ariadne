package com.ariadne.backend.storage.connection

import com.ariadne.backend.storage.credential.StorageCredentialData
import com.ariadne.backend.storage.domain.StorageCredentialType
import java.time.LocalDateTime

/**
 * 외부 Storage 연결에 성공한 뒤 내부 저장에 필요한 공통 결과다.
 *
 * Provider별 실제 인증정보는 StorageCredentialData로 분리하고,
 * StorageSource / StorageCredential에 필요한 공통 정보만 전달한다.
 */
data class StorageConnectionResult(
    val displayName: String,
    val credentialType: StorageCredentialType,
    val externalAccountId: String?,
    val scope: String?,
    val credentialData: StorageCredentialData,
    val credentialSchemaVersion: Int,
    val expiresAt: LocalDateTime? = null,
    val refreshExpiresAt: LocalDateTime? = null,
)