package com.ariadne.backend.storage.domain

/**
 * 외부 Storage 인증정보의 현재 사용 가능 상태를 표현한다.
 */
enum class StorageCredentialStatus {
    /**
     * ACTIVE → 현재 사용 가능한 Credential
     * EXPIRED → 만료됨
     * REVOKED → 사용자가 Provider에서 권한을 철회함
     * INVALID → Token 또는 인증정보가 더 이상 유효하지 않음
     */
    ACTIVE,
    EXPIRED,
    REVOKED,
    INVALID,
}